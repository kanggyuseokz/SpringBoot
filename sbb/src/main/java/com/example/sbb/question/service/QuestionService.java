package com.example.sbb.question.service;

import com.example.sbb.member.entity.Member;
import com.example.sbb.question.dto.QuestionDto;
import com.example.sbb.question.entity.Question;
import com.example.sbb.question.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    // 페이지 기능 추가
    public Page<Question> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<Sort.Order>(); // 여러 정렬 기준을 위한 리스트
        sorts.add(Sort.Order.desc("created")); // 생성일 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 페이지당 10개씩 조회 + 정렬 기능
        return questionRepository.findAll(pageable);
    }

    public Question getQuestion(Long id) {
        return questionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + id));
    }

    public void create(QuestionDto questionDto, Member member) {
        Question question = Question.builder()
                .subject(questionDto.getSubject())
                .content(questionDto.getContent())
                .author(member)
                .build();
        questionRepository.save(question);
    }
}