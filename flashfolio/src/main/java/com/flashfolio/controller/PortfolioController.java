package com.flashfolio.controller;

import com.flashfolio.dto.PortfolioResponseDto;
import com.flashfolio.entity.Portfolio;
import com.flashfolio.entity.User;
import com.flashfolio.repository.PortfolioRepository;
import com.flashfolio.service.GeminiService;
import com.flashfolio.service.MarkdownService;
import com.flashfolio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class PortfolioController {

    private final GeminiService geminiService;
    private final MarkdownService markdownService;
    private final PortfolioRepository portfolioRepository;
    private final UserService userService; // [추가] 유저 정보를 가져오기 위해 주입

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/generate")
    public String generate(@RequestParam String url, Principal principal) {
        // [수정] Principal 객체(로그인 정보)를 파라미터로 받음

        // 1. 이미 존재하는 포트폴리오인지 확인 (URL 기준)
        return portfolioRepository.findByGithubUrl(url)
                .map(p -> "redirect:/p/" + p.getId())
                .orElseGet(() -> {
                    // 2. GitHub URL을 Raw Content URL로 변환
                    String rawUrl = url.replace("github.com", "raw.githubusercontent.com")
                            .replace("/tree/", "/")
                            + "/main/README.md";
                    String readme = "";
                    try {
                        readme = RestClient.create().get().uri(rawUrl).retrieve().body(String.class);
                    } catch (Exception e) {
                        return "redirect:/?error=notfound";
                    }

                    // 3. Gemini를 통해 포트폴리오 분석 및 생성
                    Portfolio portfolio = geminiService.analyzeAndCreate(url, readme);

                    if (portfolio == null) return "redirect:/?error=ai";

                    // 4. MarkdownService를 사용하여 상세 내용 HTML 변환
                    String html = markdownService.renderHtml(portfolio.getContentHtml());

                    // [추가] 로그인한 사용자라면 User 객체 조회
                    User user = null;
                    if (principal != null) {
                        user = userService.getUser(principal.getName());
                    }

                    // 5. DB에 저장 (Builder 패턴 사용)
                    Portfolio saved = portfolioRepository.save(Portfolio.builder()
                            .user(user) // [추가] 작성자 정보 저장 (로그인 안했으면 null)
                            .githubUrl(url)
                            .title(portfolio.getTitle())
                            .summary(portfolio.getSummary())
                            .contentHtml(html)
                            .techStack(portfolio.getTechStack())
                            .features(portfolio.getFeatures())
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
                    return "portfolio/view";
                })
                .orElse("redirect:/");
    }

    // [추가] 삭제 기능
    @PostMapping("/p/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow();

        // 본인 포트폴리오인지 확인 (로그인한 상태이고, 포트폴리오 주인이 있는 경우)
        if (principal != null && portfolio.getUser() != null) {
            if (!portfolio.getUser().getUsername().equals(principal.getName())) {
                return "redirect:/?error=unauthorized";
            }
        }

        portfolioRepository.delete(portfolio);
        return "redirect:/user/mypage"; // 삭제 후 마이페이지로 이동
    }
}