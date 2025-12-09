package com.example.sbb.question.service;

import com.example.sbb.answer.entity.Answer;
import com.example.sbb.member.entity.Member;
import com.example.sbb.question.dto.QuestionDto;
import com.example.sbb.question.entity.Question;
import com.example.sbb.question.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    // 페이지 기능 추가
    public Page<Question> getList(int page, String keyword) {
        List<Sort.Order> sorts = new ArrayList<Sort.Order>(); // 여러 정렬 기준을 위한 리스트
        sorts.add(Sort.Order.desc("created")); // 생성일 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 페이지당 10개씩 조회 + 정렬 기능
        Specification<Question> specification = search(keyword); // 검색 수행
        Page<Question> questionList = questionRepository.findAll(specification, pageable);
        return questionRepository.findAll(pageable);
    }

    private Specification<Question> search(String keyword){
        return new Specification<>(){

            @Override
            public Predicate toPredicate(Root<Question> question, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                query.distinct(true); // 중복 제거
                Join<Question, Member> member1 = question.join("author", JoinType.LEFT);
                Join<Question, Answer> answer = question.join("answerList", JoinType.LEFT);
                Join<Answer, Member> member2 =  answer.join("author", JoinType.LEFT);
                return criteriaBuilder.or(criteriaBuilder.like(question.get("subject"), "%" + keyword + "%"), // 제목
                        criteriaBuilder.like(question.get("content"), "%" + keyword + "%"), // 내용
                        criteriaBuilder.like(member1.get("username"), "%" + keyword + "%"), // 질문 작성자
                        criteriaBuilder.like(member2.get("username"), "%" + keyword + "%"), // 답변 작성자
                        criteriaBuilder.like(answer.get("content"), "%" + keyword + "%"));
            }
        };
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

    public void modify(Question question, @Valid QuestionDto questionDto) {
        question.setSubject(questionDto.getSubject());
        question.setContent(questionDto.getContent());

        questionRepository.save(question);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }
}