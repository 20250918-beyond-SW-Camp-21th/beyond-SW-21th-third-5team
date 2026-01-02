package com.guincard.penghyunsuk.core.domain.auth.service;

import com.guincard.penghyunsuk.core.api.auth.dto.request.LoginRequest;
import com.guincard.penghyunsuk.core.api.auth.dto.response.TokenResponse;
import com.guincard.penghyunsuk.core.domain.auth.Repository.RefreshTokenRepository;
import com.guincard.penghyunsuk.core.domain.auth.Repository.UserRepository;
import com.guincard.penghyunsuk.core.domain.auth.entity.RefreshToken;
import com.guincard.penghyunsuk.core.domain.auth.entity.User;
import com.guincard.penghyunsuk.core.support.error.CoreException;
import com.guincard.penghyunsuk.core.support.error.ErrorCode;
import com.guincard.penghyunsuk.infra.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.expiration}")
    private long accessTokenExpiration;

    /**
     * 로그인
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 1. 로그인 ID로 사용자 조회
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CoreException(ErrorCode.INVALID_CREDENTIALS));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CoreException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getLoginId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getLoginId());

        // 4. RefreshToken DB 저장
        RefreshToken tokenEntity = RefreshToken.builder()
                .loginId(user.getLoginId())
                .token(refreshToken)
                .expiryDate(new Date(System.currentTimeMillis() + jwtTokenProvider.getRefreshExpiration()))
                .build();
        refreshTokenRepository.save(tokenEntity);

        return TokenResponse.of(accessToken, refreshToken, accessTokenExpiration);
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public TokenResponse refreshToken(String providedRefreshToken) {
        // 1. 리프레시 토큰 유효성 검사
        jwtTokenProvider.validateToken(providedRefreshToken);
        String loginId = jwtTokenProvider.getLoginIdFromJWT(providedRefreshToken);

        // 2. 저장된 refresh token 조회
        RefreshToken storedToken = refreshTokenRepository.findById(loginId)
                .orElseThrow(() -> new CoreException(ErrorCode.UNAUTHORIZED, "해당 유저로 조회되는 리프레시 토큰이 없습니다."));

        // 3. 넘어온 리프레시 토큰 값과의 일치 확인
        if (!storedToken.getToken().equals(providedRefreshToken)) {
            throw new CoreException(ErrorCode.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다.");
        }

        // 4. DB에 저장된 만료일과 현재 시간 비교
        if (storedToken.getExpiryDate().before(new Date())) {
            throw new CoreException(ErrorCode.SESSION_EXPIRED, "리프레시 토큰 유효시간이 만료되었습니다.");
        }

        // 5. 사용자 조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CoreException(ErrorCode.USER_NOT_FOUND));

        // 6. 새로운 토큰 재발급
        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getLoginId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getLoginId());

        // 7. RefreshToken 갱신
        RefreshToken tokenEntity = RefreshToken.builder()
                .loginId(user.getLoginId())
                .token(refreshToken)
                .expiryDate(new Date(System.currentTimeMillis() + jwtTokenProvider.getRefreshExpiration()))
                .build();
        refreshTokenRepository.save(tokenEntity);

        return TokenResponse.of(accessToken, refreshToken, accessTokenExpiration);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String refreshToken) {
        // 1. refresh token의 서명 및 만료 검증
        jwtTokenProvider.validateToken(refreshToken);
        String loginId = jwtTokenProvider.getLoginIdFromJWT(refreshToken);

        // 2. DB에서 RefreshToken 삭제
        refreshTokenRepository.deleteById(loginId);
    }
}
