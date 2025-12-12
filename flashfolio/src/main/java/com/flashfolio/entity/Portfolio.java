package com.flashfolio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor // JPA는 기본 생성자가 필수입니다.
public class Portfolio {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String githubUrl;

    private String title;

    @Column(length = 1000)
    private String summary;

    @Lob
    private String contentHtml;

    @ElementCollection
    private List<String> techStack;

    @ElementCollection
    private List<String> features;

    private LocalDateTime createdAt;

    // 생성자
    public Portfolio(String githubUrl, String title, String summary, String contentHtml, List<String> techStack, List<String> features) {
        this.githubUrl = githubUrl;
        this.title = title;
        this.summary = summary;
        this.contentHtml = contentHtml;
        this.techStack = techStack;
        this.features = features;
        this.createdAt = LocalDateTime.now();
    }
}