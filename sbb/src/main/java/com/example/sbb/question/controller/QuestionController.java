package com.example.sbb.question.controller;

import com.example.sbb.answer.dto.AnswerDto;
import com.example.sbb.member.entity.Member;
import com.example.sbb.member.service.MemberService;
import com.example.sbb.question.dto.QuestionDto;
import com.example.sbb.question.entity.Question;
import com.example.sbb.question.repository.QuestionRepository;
import com.example.sbb.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page) { // 페이지 기능 추가
        Page<Question> paging = questionService.getList(page);
        model.addAttribute("paging", paging);
        return "question/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        model.addAttribute("answerDto", new AnswerDto());
        return "question/detail";
    }

    @GetMapping("/create")
    public String createQuestion(QuestionDto questionDto) {
        return "question/inputForm";
    }

    @PostMapping("/create")
    public String createQuestion(@Valid QuestionDto questionDto,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("questionDto", questionDto);
            return "question/inputForm";
        }

        Member member = memberService.getMember(principal.getName());

        questionService.create(questionDto, member);
        return "redirect:/question/list";
    }

}