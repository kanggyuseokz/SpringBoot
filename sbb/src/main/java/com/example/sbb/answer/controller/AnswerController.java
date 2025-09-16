package com.example.sbb.answer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/answer")
public class AnswerController {

    @PostMapping("/create/{id}")
    public String create(@PathVariable("id") Long id, @RequestParam("contents") String contents) {
        log.info("id: {}", id);
        return "redirect:/question/detail/" + id;

    }


}
