package com.example.sbb.member.controller;

import com.example.sbb.member.dto.MemberDto;
import com.example.sbb.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;


    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signUp(@Valid MemberDto memberDto, BindingResult bindingResult, Model model) {
        log.info("=======> memberDto: {}", memberDto);

        if (!memberDto.getPassword1().equals(memberDto.getPassword2())) {
            // 필드명, 오류 코드, 오류 메시지
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "member/signup";
        }

        if (bindingResult.hasErrors()) return "member/signup";

        try {
            memberService.create(memberDto);
        } catch (DataIntegrityViolationException e) {
            log.info("--------------------- 회원 가입 실패: 이미 등록된 사용자");
            model.addAttribute("errorMessage", "이미 등록된 사용자입니다.");
            return "member/signup";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/login";
    }

//    @GetMapping("/logout")
//    public String performLogout(HttpServletRequest request, HttpServletResponse response) {
//        log.info("=======> logout");
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            new SecurityContextLogoutHandler().logout(request, response, authentication);
//        }
//        return "redirect:/";
//    }

}
