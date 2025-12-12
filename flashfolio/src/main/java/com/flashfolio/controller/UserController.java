package com.flashfolio.controller;

import com.flashfolio.entity.User;
import com.flashfolio.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // === 회원가입 ===
    @GetMapping("/signup")
    public String signupForm() {
        return "signup"; // signup.html 템플릿 필요
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String email,
                         Model model) {
        try {
            userService.signup(username, password, email);
            return "redirect:/login"; // 가입 성공 시 로그인 페이지로
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    // === 로그인 ===
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // login.html 템플릿 필요
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {

        User loginUser = userService.login(username, password);

        if (loginUser == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login";
        }

        // 로그인 성공 처리: 세션에 사용자 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", loginUser);

        return "redirect:/"; // 메인 페이지로 이동
    }

    // === 로그아웃 ===
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 제거
        }
        return "redirect:/";
    }
}