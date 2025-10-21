package com.example.sbb.question.repository;

import com.example.sbb.question.entity.Question;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findBySubjectLike(String keyword);

    @Nonnull
    Page <Question> findAll(@Nonnull Pageable pageable);
}
