package com.example.sbb.question.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Data
@Builder
public class QuestionDto {
    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String subject;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;
}
