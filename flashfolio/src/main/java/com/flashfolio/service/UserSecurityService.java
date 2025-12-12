package com.flashfolio.service;

import com.flashfolio.entity.User;
import com.flashfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    // 스프링 시큐리티가 로그인 버튼을 누르면 이 메서드를 자동으로 실행합니다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DB에서 유저 찾기
        Optional<User> _siteUser = this.userRepository.findByUsername(username);

        // 2. 없으면 에러 발생 (로그인 실패 처리됨)
        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 3. 찾았으면 유저 정보 가져오기
        User siteUser = _siteUser.get();

        // 4. 권한 부여 (기본적으로 USER 권한 줌)
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // 5. 스프링 시큐리티에게 "이 사람이 맞는 유저입니다"라고 정보 전달
        // (비밀번호 검사는 스프링 시큐리티가 알아서 함)
        return new org.springframework.security.core.userdetails.User(siteUser.getUsername(), siteUser.getPassword(), authorities);
    }
}