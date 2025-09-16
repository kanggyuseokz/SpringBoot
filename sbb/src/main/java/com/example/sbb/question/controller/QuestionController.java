package com.example.sbb.question.controller;

import com.example.sbb.question.entity.Question;
import com.example.sbb.question.repository.QuestionRepository;
import com.example.sbb.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/question/list")
    public String list(Model model){
        List<Question> questionList = questionService.getList();
        log.info("list: {}", questionList);
        model.addAttribute("questionList", questionList);
        return "question/list";
    }

    @GetMapping("/question/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        log.info("id: {}", id);
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/detail";
    }

}

