package com.flashfolio.service;

import com.flashfolio.entity.User;
import com.flashfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ... (기존 signup, login, isUsernameTaken 메서드 유지) ...
    @Transactional
    public User signup(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    // [추가] 비밀번호 변경 로직
    @Transactional
    public void changePassword(User user, String newPassword) {
        // 영속성 컨텍스트를 위해 다시 조회
        User persistentUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        persistentUser.setPassword(passwordEncoder.encode(newPassword));
    }

    // [추가] 현재 비밀번호 확인 로직
    public boolean checkPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }
}