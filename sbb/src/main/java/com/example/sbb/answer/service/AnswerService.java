package com.example.sbb.answer.service;

import com.example.sbb.answer.entity.Answer;
import com.example.sbb.answer.repository.AnswerRepository;
import com.example.sbb.question.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRespoitory;

    public void create(Question question, String content) {
        Answer answer = Answer.builder()
                .content(content)
                .question(question)
                .build();

        answerRespoitory.save(answer);
    }
}