package com.flashfolio.service;

import com.flashfolio.entity.Portfolio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
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

        // 프롬프트 강화: 기술 스택 추론(INFER) 지시 추가
        String prompt = """
            You are a professional tech portfolio editor. Analyze the provided GitHub README content.
            Respond strictly in valid JSON format. Do not include markdown code blocks (like ```json).
            
            Instructions:
            1. Title: Extract the project name.
            2. Summary: Write a concise, professional one-sentence summary in Korean.
            3. Tech Stack: Extract used technologies. If not explicitly listed, INFER them from build commands (e.g., 'mvn'->Java/Spring, 'pip'->Python), file extensions, or dependencies mentioned.
            4. Features: Summarize 3-5 key features in Korean.
            5. Content: Rewrite the main description into clean HTML format (using <h3>, <p>, <ul>, <li> tags). Do not use <h1> or <h2>. Translate the content to Korean if it's in English.
            
            JSON Structure:
            {
                "title": "Project Title",
                "summary": "한 줄 요약",
                "techStack": ["Java", "Spring Boot", "MySQL"],
                "features": ["기능 1", "기능 2"],
                "content": "<div>...HTML content...</div>"
            }
            
            README Content:
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
            JsonNode candidates = root.path("candidates");
            if (candidates.isMissingNode() || candidates.isEmpty()) {
                log.error("No candidates in Gemini response");
                return null;
            }

            String text = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
            // JSON 마크다운 제거 (혹시 AI가 붙였을 경우 대비)
            String cleanJson = text.replaceAll("```json", "").replaceAll("```", "").trim();

            JsonNode data = objectMapper.readTree(cleanJson);

            List<String> techStack = new ArrayList<>();
            if (data.has("techStack")) {
                data.path("techStack").forEach(node -> techStack.add(node.asText()));
            }

            // 기술 스택이 비어있을 경우 기본값 처리
            if (techStack.isEmpty()) {
                techStack.add("General");
            }

            List<String> features = new ArrayList<>();
            if (data.has("features")) {
                data.path("features").forEach(node -> features.add(node.asText()));
            }

            return new Portfolio(
                    githubUrl,
                    data.path("title").asText(githubUrl), // 제목 없으면 URL로 대체
                    data.path("summary").asText("프로젝트 설명이 없습니다."),
                    data.path("content").asText(""),
                    techStack,
                    features
            );
        } catch (Exception e) {
            log.error("Parsing Error: Response was -> {}", jsonResponse, e);
            return null;
        }
    }
}