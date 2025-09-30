package com.example.sbb.answer.service;

import com.example.sbb.answer.dto.AnswerDto;
import com.example.sbb.answer.entity.Answer;
import com.example.sbb.answer.repository.AnswerRepository;
import com.example.sbb.question.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public void create(Question question, AnswerDto answerDto) {
        Answer answer = Answer.builder()
                .content(answerDto.getContent())
                .question(question)
                .build();

        answerRepository.save(answer);
    }
}