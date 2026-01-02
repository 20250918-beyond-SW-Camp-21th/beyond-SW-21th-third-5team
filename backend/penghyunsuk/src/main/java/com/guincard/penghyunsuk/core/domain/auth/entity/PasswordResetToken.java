package com.guincard.penghyunsuk.core.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token",
        indexes = {
                @Index(name="idx_prt_user_created", columnList="userId, createdAt")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // 6자리 코드를 해시로 저장
    @Column(nullable = false, length = 64)
    private String codeHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static PasswordResetToken create(Long userId, String codeHash, Duration ttl) {
        PasswordResetToken t = new PasswordResetToken();
        t.userId = userId;
        t.codeHash = codeHash;
        t.createdAt = LocalDateTime.now();
        t.expiresAt = t.createdAt.plus(ttl);
        return t;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public void markUsed() {
        this.usedAt = LocalDateTime.now();
    }
}
