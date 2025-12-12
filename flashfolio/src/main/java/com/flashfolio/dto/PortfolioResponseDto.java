package com.flashfolio.dto;

import com.flashfolio.entity.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponseDto {
    private Long id;
    private String githubUrl;
    private String title;
    private String summary;
    private String contentHtml;
    private List<String> techStack;
    private List<String> features;

    // 새로 추가된 필드들
    private List<TroubleShootingDto> troubleshooting;
    private String architecture;
    private String gettingStarted;

    // 내부 DTO 클래스
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TroubleShootingDto {
        private String problem;
        private String solution;
    }

    // Entity -> DTO 변환 메서드
    public static PortfolioResponseDto from(Portfolio portfolio) {
        // 트러블 슈팅 리스트 변환 로직
        List<TroubleShootingDto> tsDtoList = null;
        if (portfolio.getTroubleshooting() != null) {
            tsDtoList = portfolio.getTroubleshooting().stream()
                    .map(t -> new TroubleShootingDto(t.getProblem(), t.getSolution()))
                    .collect(Collectors.toList());
        }

        return PortfolioResponseDto.builder()
                .id(portfolio.getId())
                .githubUrl(portfolio.getGithubUrl())
                .title(portfolio.getTitle())
                .summary(portfolio.getSummary())
                .contentHtml(portfolio.getContentHtml())
                .techStack(portfolio.getTechStack())
                .features(portfolio.getFeatures())

                // 새로운 필드 매핑
                .troubleshooting(tsDtoList)
                .architecture(portfolio.getArchitecture())
                .gettingStarted(portfolio.getGettingStarted())
                .build();
    }
}