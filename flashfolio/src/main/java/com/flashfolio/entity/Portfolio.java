package com.flashfolio.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String githubUrl;

    private String title;

    @Column(length = 1000)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String contentHtml;

    @ElementCollection
    private List<String> techStack;

    @ElementCollection
    private List<String> features;

    // --- [신규 추가] 트러블 슈팅, 아키텍처, 실행 방법 필드 ---

    @ElementCollection
    @CollectionTable(name = "portfolio_troubleshooting", joinColumns = @JoinColumn(name = "portfolio_id"))
    private List<TroubleShooting> troubleshooting;

    @Column(columnDefinition = "TEXT")
    private String architecture;

    @Column(columnDefinition = "TEXT")
    private String gettingStarted;

    // --- [신규 추가] 트러블 슈팅 정보를 담을 내부 클래스 ---
    @Embeddable
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TroubleShooting {
        @Column(columnDefinition = "TEXT")
        private String problem;

        @Column(columnDefinition = "TEXT")
        private String solution;
    }

    // 생성자 업데이트 (모든 필드 포함)
    @Builder // 빌더 패턴을 쓰면 생성자 순서 헷갈릴 일이 없어 추천합니다 (선택사항)
    public Portfolio(User user, String githubUrl, String title, String summary, String contentHtml,
                     List<String> techStack, List<String> features,
                     List<TroubleShooting> troubleshooting, String architecture, String gettingStarted) {
        this.user = user;
        this.githubUrl = githubUrl;
        this.title = title;
        this.summary = summary;
        this.contentHtml = contentHtml;
        this.techStack = techStack;
        this.features = features;
        this.troubleshooting = troubleshooting;
        this.architecture = architecture;
        this.gettingStarted = gettingStarted;
    }
}