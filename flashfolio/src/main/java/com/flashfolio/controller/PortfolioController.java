package com.flashfolio.controller;

import com.flashfolio.dto.PortfolioResponseDto;
import com.flashfolio.entity.Portfolio;
import com.flashfolio.repository.PortfolioRepository;
import com.flashfolio.service.GeminiService;
import com.flashfolio.service.MarkdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@Controller
@RequiredArgsConstructor
public class PortfolioController {

    private final GeminiService geminiService;
    private final MarkdownService markdownService;
    private final PortfolioRepository portfolioRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/generate")
    public String generate(@RequestParam String url) {
        // 이미 생성된 포트폴리오가 있다면 바로 이동
        return portfolioRepository.findByGithubUrl(url)
                .map(p -> "redirect:/p/" + p.getId())
                .orElseGet(() -> {
                    // 1. GitHub URL을 Raw Content URL로 변환
                    String rawUrl = url.replace("github.com", "raw.githubusercontent.com")
                            .replace("/tree/", "/")
                            + "/main/README.md";
                    String readme = "";
                    try {
                        readme = RestClient.create().get().uri(rawUrl).retrieve().body(String.class);
                    } catch (Exception e) {
                        return "redirect:/?error=notfound";
                    }

                    // 2. Gemini를 통해 포트폴리오 분석 및 생성
                    // (GeminiService에서 반환하는 Portfolio 객체에 새 필드 데이터가 들어있다고 가정)
                    Portfolio portfolio = geminiService.analyzeAndCreate(url, readme);

                    if (portfolio == null) return "redirect:/?error=ai";

                    // 3. MarkdownService를 사용하여 상세 내용 HTML 변환
                    String html = markdownService.renderHtml(portfolio.getContentHtml());

                    // 4. DB에 저장 (Builder 패턴 사용)
                    // 엔티티에 추가한 새 필드들(troubleshooting 등)도 함께 저장합니다.
                    Portfolio saved = portfolioRepository.save(Portfolio.builder()
                            .githubUrl(url)
                            .title(portfolio.getTitle())
                            .summary(portfolio.getSummary())
                            .contentHtml(html)
                            .techStack(portfolio.getTechStack())
                            .features(portfolio.getFeatures())
                            // [신규 필드 매핑]
                            .troubleshooting(portfolio.getTroubleshooting())
                            .architecture(portfolio.getArchitecture())
                            .gettingStarted(portfolio.getGettingStarted())
                            .build());

                    return "redirect:/p/" + saved.getId();
                });
    }

    @GetMapping("/p/{id}")
    public String view(@PathVariable Long id, Model model) {
        return portfolioRepository.findById(id)
                .map(p -> {
                    // Entity -> DTO 변환 후 모델에 추가
                    model.addAttribute("portfolio", PortfolioResponseDto.from(p));
                    // 앞서 만들어드린 HTML 파일 경로가 templates/portfolio/view.html 이라면 아래처럼 수정
                    return "portfolio/view";
                })
                .orElse("redirect:/");
    }
}