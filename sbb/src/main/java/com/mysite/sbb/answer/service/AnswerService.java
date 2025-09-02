package com.mysite.sbb.answer.service;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.answer.repository.AnswerRespository;
import com.mysite.sbb.question.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AnswerService {

  private final AnswerRespository answerRespository;

  public void create(Question question, String content) {
    Answer answer = Answer.builder()
        .content(content)
        .question(question)
        .build();

    answerRespository.save(answer);
  }
}
