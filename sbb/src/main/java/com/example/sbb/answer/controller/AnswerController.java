package com.example.sbb.answer.controller;

import com.example.sbb.answer.dto.AnswerDto;
import com.example.sbb.answer.repository.AnswerRepository;
import com.example.sbb.answer.service.AnswerService;
import com.example.sbb.member.entity.Member;
import com.example.sbb.member.service.MemberService;
import com.example.sbb.question.entity.Question;
import com.example.sbb.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final MemberService memberService;
//    @PostMapping("/create/{id}")
//    public String createAnswer(@PathVariable Long id, @RequestParam(value = "content") String content) {
//        Question question = questionService.getQuestion(id);
//        answerService.create(question, content);
//        log.info("Creating answer for question ID: {}, content: {}", id, content);
//        return "redirect:/question/detail/" + id;
//    }


    @PostMapping("/create/{id}")
    public String createAnswer(@PathVariable Long id,
                               @Valid AnswerDto answerDto,
                               BindingResult bindingResult,
                               Principal principal,
                               Model model) { // Model 객체 추가 (오류 시 재전달용)

        Question question = questionService.getQuestion(id);
        Member member = memberService.getMember(principal.getName());
        // 1. 유효성 검사 오류 처리
        if (bindingResult.hasErrors()) {
            // 오류가 있을 경우, 상세 페이지에 필요한 객체를 다시 Model에 담아
            model.addAttribute("question", question);

            // 상세 페이지 템플릿으로 돌아갑니다.
            return "question/detail";
        }

        // 2. 서비스 로직 실행 (오류가 없을 경우)
        answerService.create(question, answerDto, member);
        log.info("Creating answer for question ID: {}, content: {}", id, answerDto.getContent());

        // 3. 리다이렉트
        return String.format("redirect:/question/detail/%d", id);
    }
}
