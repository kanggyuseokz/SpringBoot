package com.flashfolio.controller;

import com.flashfolio.dto.UserCreateForm;
import com.flashfolio.entity.User;
import com.flashfolio.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // === 아이디 중복 확인 API (AJAX용) ===
    @GetMapping("/user/check")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean isTaken = userService.isUsernameTaken(username);
        // available: true면 사용 가능(중복 아님), false면 사용 불가(중복)
        return ResponseEntity.ok(Map.of("available", !isTaken));
    }

    // === 회원가입 ===
    @GetMapping("/signup")
    public String signupForm(UserCreateForm userCreateForm) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        if (!userCreateForm.getPassword().equals(userCreateForm.getPasswordRepeat())) {
            bindingResult.rejectValue("passwordRepeat", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
            return "signup";
        }

        try {
            userService.signup(userCreateForm.getUsername(), userCreateForm.getPassword(), userCreateForm.getEmail());
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup";
        } catch (Exception e) {
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup";
        }

        return "redirect:/login";
    }

    // === 로그인 ===
    @GetMapping("/login")
    public String loginForm() {
        return "login";
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

        HttpSession session = request.getSession();
        session.setAttribute("loginUser", loginUser);

        return "redirect:/";
    }

    // === 로그아웃 ===
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}