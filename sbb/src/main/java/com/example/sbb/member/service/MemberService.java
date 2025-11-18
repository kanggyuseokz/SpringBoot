package com.example.sbb.member.service;

import com.example.sbb.member.dto.MemberDto;
import com.example.sbb.member.entity.Member;
import com.example.sbb.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void create(@Valid MemberDto memberDto) {
        Member member = Member.builder()
                .username(memberDto.getUsername())
                .password(passwordEncoder.encode(memberDto.getPassword1())) // 비밀번호 암호화
                .email(memberDto.getEmail())
                .gender(memberDto.getGender())
                .department(memberDto.getDepartment())
                .registration(memberDto.getRegistration())
                .build();

        memberRepository.save(member);
    }

    public Member getMember(String name){
        Member member = memberRepository.findByUsername(name)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않음 " + name));
        return member;
    }
}
