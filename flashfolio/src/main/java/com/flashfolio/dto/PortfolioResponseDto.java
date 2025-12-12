package com.flashfolio.dto;

import com.flashfolio.entity.Portfolio;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PortfolioResponseDto {
    private Long id;
    private String githubUrl;
    private String title;
    private String summary;
    private String contentHtml;
    private List<String> techStack;
    private List<String> features;

    // Entity -> DTO 변환 메서드
    public static PortfolioResponseDto from(Portfolio portfolio) {
        return PortfolioResponseDto.builder()
                .id(portfolio.getId())
                .githubUrl(portfolio.getGithubUrl())
                .title(portfolio.getTitle())
                .summary(portfolio.getSummary())
                .contentHtml(portfolio.getContentHtml())
                .techStack(portfolio.getTechStack())
                .features(portfolio.getFeatures())
                .build();
    }
}