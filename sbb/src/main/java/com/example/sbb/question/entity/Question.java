package com.example.sbb.question.entity;

import com.example.sbb.answer.entity.Answer;
import com.example.sbb.audit.BaseEntity;
import com.example.sbb.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(length = 200, nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answerList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;
}