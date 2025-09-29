package com.example.sbb.question.controller;

import com.example.sbb.question.dto.QuestionDto;
import com.example.sbb.question.entity.Question;
import com.example.sbb.question.repository.QuestionRepository;
import com.example.sbb.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Question> questionList = questionService.getList();
        model.addAttribute("questionList", questionList);
        return "question/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/detail";
    }

    @GetMapping("/create")
    public String createQuestion(QuestionDto questionDto) {
        return "question/inputForm";
    }

    @PostMapping("/create")
    public String createQuestion(@Valid QuestionDto questionDto, BindingResult bindingResult) {
//  public String createQuestion(QuestionFormDto questionFormDto) {
//  public String createQuestion(@RequestParam(value = "subject") String subject,
//                               @RequestParam(value = "content") String content) {
//    QuestionFormDto questionFormDto = QuestionFormDto.builder()
//        .subject(subject)
//        .content(content)
//        .build();

        if (bindingResult.hasErrors()) {
            return "question/inputForm";
        }

        // 필요시 예외 처리 추가
        questionService.create(questionDto);
        return "redirect:/question/list";
    }

}