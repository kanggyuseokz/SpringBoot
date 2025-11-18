package com.example.sbb.answer.service;

import com.example.sbb.answer.dto.AnswerDto;
import com.example.sbb.answer.entity.Answer;
import com.example.sbb.answer.repository.AnswerRepository;
import com.example.sbb.member.entity.Member;
import com.example.sbb.question.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public void create(Question question, AnswerDto answerDto, Member member) {
        Answer answer = Answer.builder()
                .content(answerDto.getContent())
                .question(question)
                .author(member)
                .build();

        answerRepository.save(answer);
    }
}