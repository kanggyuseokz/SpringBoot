package com.example.sbb.answer.controller;

import com.example.sbb.answer.repository.AnswerRepository;
import com.example.sbb.answer.service.AnswerService;
import com.example.sbb.question.entity.Question;
import com.example.sbb.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/answer")
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    @PostMapping("/create/{id}")
    public String create(@PathVariable("id") Long id, @RequestParam("contents") String contents) {
        Question question = questionService.getQuestion(id);
        answerService.create(question, contents);
        log.info("===============>id: {}, {}", id, contents);
        return "redirect:/question/detail/" + id;
    }


}
