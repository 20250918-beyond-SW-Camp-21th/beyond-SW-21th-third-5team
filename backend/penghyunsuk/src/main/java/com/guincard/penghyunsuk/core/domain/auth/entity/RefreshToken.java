package com.guincard.penghyunsuk.core.domain.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @Comment("로그인 ID (PK)")
    private String loginId;

    @Comment("리프레시 토큰")
    private String token;

    @Comment("만료일시")
    private Date expiryDate;

    @Builder
    public RefreshToken(String loginId, String token, Date expiryDate) {
        this.loginId = loginId;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}