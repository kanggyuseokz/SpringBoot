package com.flashfolio.controller;

import com.flashfolio.dto.PortfolioResponseDto; // DTO Import
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
        return portfolioRepository.findByGithubUrl(url)
                .map(p -> "redirect:/p/" + p.getId())
                .orElseGet(() -> {
                    // GitHub URL을 Raw Content URL로 변환
                    String rawUrl = url.replace("github.com", "raw.githubusercontent.com")
                            .replace("/tree/", "/")
                            + "/main/README.md";
                    String readme = "";
                    try {
                        readme = RestClient.create().get().uri(rawUrl).retrieve().body(String.class);
                    } catch (Exception e) {
                        return "redirect:/?error=notfound";
                    }

                    // Gemini를 통해 포트폴리오 분석 및 생성
                    Portfolio portfolio = geminiService.analyzeAndCreate(url, readme);

                    if (portfolio == null) return "redirect:/?error=ai";

                    // MarkdownService를 사용하여 HTML 변환
                    String html = markdownService.renderHtml(portfolio.getContentHtml());

                    Portfolio saved = portfolioRepository.save(new Portfolio(
                            url, portfolio.getTitle(), portfolio.getSummary(), html,
                            portfolio.getTechStack(), portfolio.getFeatures()
                    ));

                    return "redirect:/p/" + saved.getId();
                });
    }

    @GetMapping("/p/{id}")
    public String view(@PathVariable Long id, Model model) {
        return portfolioRepository.findById(id)
                .map(p -> {
                    // Entity -> DTO 변환 후 모델에 추가
                    model.addAttribute("portfolio", PortfolioResponseDto.from(p));
                    return "portfolio";
                })
                .orElse("redirect:/");
    }
}