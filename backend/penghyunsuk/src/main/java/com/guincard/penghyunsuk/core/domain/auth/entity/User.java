package com.guincard.penghyunsuk.core.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 ID")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Comment("로그인 ID")
    private String loginId;

    @Column(nullable = false, unique = true, length = 100)
    @Comment("이메일")
    private String email;

    @Column(nullable = false)
    @Comment("비밀번호 (암호화)")
    private String password;

    @Column(nullable = false, length = 30)
    @Comment("닉네임")
    private String nickname;

    @Column(nullable = false, updatable = false)
    @Comment("가입일시")
    private LocalDateTime createdAt;

    @Builder
    public User(String loginId, String email, String password, String nickname) {
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 닉네임 변경
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}