package com.flashfolio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // User와 연관관계 추가
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

    // 생성자 수정 (User 추가)
    public Portfolio(User user, String githubUrl, String title, String summary, String contentHtml, List<String> techStack, List<String> features) {
        this.user = user; // 사용자 정보 저장
        this.githubUrl = githubUrl;
        this.title = title;
        this.summary = summary;
        this.contentHtml = contentHtml;
        this.techStack = techStack;
        this.features = features;
    }

    // 임시 생성자 (비로그인용 - User 없이 생성 가능하도록 유지하거나, 비로그인 시 저장을 안 하므로 이 생성자는 사실상 DTO 변환용 등으로 쓰일 수 있음)
    public Portfolio(String githubUrl, String title, String summary, String contentHtml, List<String> techStack, List<String> features) {
        this.githubUrl = githubUrl;
        this.title = title;
        this.summary = summary;
        this.contentHtml = contentHtml;
        this.techStack = techStack;
        this.features = features;
    }
}