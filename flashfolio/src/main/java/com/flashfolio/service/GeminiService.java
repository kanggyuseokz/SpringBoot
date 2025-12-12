package com.flashfolio.service;

import com.flashfolio.entity.Portfolio;
import com.flashfolio.entity.Portfolio.TroubleShooting;
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

        // 프롬프트 강화: 트러블슈팅, 아키텍처, 실행방법 추가 요청
        String prompt = """
            You are a professional tech portfolio editor. Analyze the provided GitHub README content.
            Respond strictly in valid JSON format. Do not include markdown code blocks (like ```json).
            
            Instructions:
            1. Title: Extract the project name.
            2. Summary: Write a concise, professional one-sentence summary in Korean.
            3. Tech Stack: Extract used technologies. If not explicitly listed, INFER them from build commands (e.g., 'mvn'->Java/Spring, 'pip'->Python).
            4. Features: Summarize 3-5 key features in Korean.
            5. Troubleshooting: Extract 2-3 technical challenges and solutions (problem, solution) in Korean. If none, infer generic ones based on tech stack.
            6. Architecture: Describe the system architecture or data flow in Korean.
            7. Getting Started: Provide installation or execution commands (e.g., 'npm start').
            8. Content: Rewrite the main description into clean HTML format (using <h3>, <p>, <ul>, <li> tags). Do not use <h1> or <h2>. Translate to Korean.
            
            JSON Structure:
            {
                "title": "Project Title",
                "summary": "한 줄 요약",
                "techStack": ["Java", "Spring Boot"],
                "features": ["기능 1", "기능 2"],
                "troubleshooting": [
                    {"problem": "문제점 설명", "solution": "해결 방안"}
                ],
                "architecture": "아키텍처 설명",
                "gettingStarted": "npm install && npm start",
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
            // JSON 마크다운 제거
            String cleanJson = text.replaceAll("```json", "").replaceAll("```", "").trim();

            JsonNode data = objectMapper.readTree(cleanJson);

            // 1. Tech Stack 파싱
            List<String> techStack = new ArrayList<>();
            if (data.has("techStack")) {
                data.path("techStack").forEach(node -> techStack.add(node.asText()));
            }
            if (techStack.isEmpty()) techStack.add("General");

            // 2. Features 파싱
            List<String> features = new ArrayList<>();
            if (data.has("features")) {
                data.path("features").forEach(node -> features.add(node.asText()));
            }

            // 3. Troubleshooting 파싱 (신규)
            List<TroubleShooting> troubleshooting = new ArrayList<>();
            if (data.has("troubleshooting")) {
                data.path("troubleshooting").forEach(node -> {
                    String problem = node.path("problem").asText();
                    String solution = node.path("solution").asText();
                    if (!problem.isEmpty() && !solution.isEmpty()) {
                        troubleshooting.add(new TroubleShooting(problem, solution));
                    }
                });
            }

            // 4. Portfolio 객체 생성 (Builder 사용 추천)
            return Portfolio.builder()
                    .githubUrl(githubUrl)
                    .title(data.path("title").asText(githubUrl))
                    .summary(data.path("summary").asText("프로젝트 설명이 없습니다."))
                    .contentHtml(data.path("content").asText(""))
                    .techStack(techStack)
                    .features(features)
                    .troubleshooting(troubleshooting) // 신규 필드
                    .architecture(data.path("architecture").asText("")) // 신규 필드
                    .gettingStarted(data.path("gettingStarted").asText("")) // 신규 필드
                    .build();

        } catch (Exception e) {
            log.error("Parsing Error: Response was -> {}", jsonResponse, e);
            return null;
        }
    }
}