package com.flashfolio.service;

import com.flashfolio.entity.Portfolio; // entity 패키지 위치 확인 필
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient; // Spring Boot 3.2+ 필요
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    public Portfolio analyzeAndCreate(String githubUrl, String readmeContent) {
        // 토큰 제한을 고려하여 입력 길이 제한
        String safeContent = readmeContent.length() > 15000 ? readmeContent.substring(0, 15000) : readmeContent;

        String prompt = """
            Analyze this README for a developer portfolio website.
            Respond strictly in JSON format. No markdown blocks.
            
            JSON Structure:
            {
                "title": "Project Title",
                "summary": "One sentence summary (Korean)",
                "techStack": ["Java", "Spring"],
                "features": ["Feature 1", "Feature 2"],
                "content": "Full markdown description (Korean)"
            }
            
            README:
            """ + safeContent;

        Map<String, Object> request = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        try {
            String response = restClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            return parseResponse(githubUrl, response);
        } catch (Exception e) {
            log.error("Gemini API Error", e);
            return null;
        }
    }

    private Portfolio parseResponse(String githubUrl, String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            // Gemini 응답 구조에 따라 안전하게 파싱
            JsonNode candidates = root.path("candidates");
            if (candidates.isMissingNode() || candidates.isEmpty()) {
                log.error("No candidates in Gemini response");
                return null;
            }

            String text = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
            String cleanJson = text.replace("```json", "").replace("```", "").trim();

            JsonNode data = objectMapper.readTree(cleanJson);

            List<String> techStack = new ArrayList<>();
            if (data.has("techStack")) {
                data.path("techStack").forEach(node -> techStack.add(node.asText()));
            }

            List<String> features = new ArrayList<>();
            if (data.has("features")) {
                data.path("features").forEach(node -> features.add(node.asText()));
            }

            // Portfolio Entity 생성자 시그니처와 일치해야 함
            return new Portfolio(
                    githubUrl,
                    data.path("title").asText(),
                    data.path("summary").asText(),
                    data.path("content").asText(),
                    techStack,
                    features
            );
        } catch (Exception e) {
            log.error("Parsing Error", e);
            return null;
        }
    }
}