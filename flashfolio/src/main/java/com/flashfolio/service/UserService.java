package com.flashfolio.service;

import com.flashfolio.entity.User;
import com.flashfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 회원가입 처리
     */
    @Transactional
    public Long signup(String username, String password, String email) {
        // 중복 검증
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화는 추후 Spring Security 도입 시 추가 (현재는 평문 저장)
        User user = new User(username, password, email);
        return userRepository.save(user).getId();
    }

    /**
     * 로그인 처리
     * @return 로그인 성공 시 User 객체 반환, 실패 시 null
     */
    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }
}