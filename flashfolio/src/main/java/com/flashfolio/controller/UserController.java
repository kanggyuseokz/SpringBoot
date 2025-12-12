package com.flashfolio.controller;

import com.flashfolio.dto.PasswordChangeForm; // 추가
import com.flashfolio.dto.UserCreateForm;
import com.flashfolio.entity.Portfolio;
import com.flashfolio.entity.User;
import com.flashfolio.repository.PortfolioRepository;
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

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PortfolioRepository portfolioRepository;

    // ... (기존 checkUsername, signup, login, logout 관련 메서드들 유지) ...
    @GetMapping("/user/check")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean isTaken = userService.isUsernameTaken(username);
        return ResponseEntity.ok(Map.of("available", !isTaken));
    }

    @GetMapping("/signup")
    public String signupForm(UserCreateForm userCreateForm) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "signup";
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

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletRequest request, Model model) {
        User loginUser = userService.login(username, password);
        if (loginUser == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login";
        }
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", loginUser);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return "redirect:/";
    }

    // === [수정] 마이페이지 (비밀번호 폼 객체 추가) ===
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model, PasswordChangeForm passwordChangeForm) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        List<Portfolio> portfolios = portfolioRepository.findByUser(loginUser);
        model.addAttribute("portfolios", portfolios);

        return "mypage";
    }

    // === [추가] 비밀번호 변경 처리 ===
    @PostMapping("/user/change-password")
    public String changePassword(@Valid PasswordChangeForm form, BindingResult bindingResult,
                                 HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 1. 새 비밀번호 일치 확인
        if (!form.getNewPassword().equals(form.getNewPasswordRepeat())) {
            bindingResult.rejectValue("newPasswordRepeat", "passwordInCorrect", "새 비밀번호가 일치하지 않습니다.");
        }

        // 2. 현재 비밀번호 확인
        if (!userService.checkPassword(loginUser, form.getCurrentPassword())) {
            bindingResult.rejectValue("currentPassword", "passwordInCorrect", "현재 비밀번호가 틀렸습니다.");
        }

        if (bindingResult.hasErrors()) {
            // 에러 발생 시 마이페이지로 돌아가되, 포트폴리오 목록도 같이 보내줘야 함
            List<Portfolio> portfolios = portfolioRepository.findByUser(loginUser);
            model.addAttribute("portfolios", portfolios);
            return "mypage";
        }

        userService.changePassword(loginUser, form.getNewPassword());

        // 비밀번호 변경 후 로그아웃 처리 (보안상 재로그인 유도)
        session.invalidate();

        return "redirect:/login?changed=true"; // 로그인 페이지로 이동하며 알림용 파라미터 전달
    }
}