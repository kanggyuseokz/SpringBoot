package com.flashfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/**").permitAll()) // AntPathRequestMatcher 제거, 문자열 패턴 직접 사용
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/h2-console/**")) // AntPathRequestMatcher 제거
                .headers((headers) -> headers
                        .addHeaderWriter(new org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter(
                                org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}