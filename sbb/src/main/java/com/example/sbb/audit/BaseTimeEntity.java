package com.example.sbb.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
public class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)          // 수정 불가
    private LocalDateTime created;

    @LastModifiedDate                   // 최종 수정 시점
    private LocalDateTime updated;      // 수정일
}
