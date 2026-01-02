package com.guincard.penghyunsuk.core.api.auth.controller;

import com.guincard.penghyunsuk.core.api.auth.dto.request.LoginRequest;
import com.guincard.penghyunsuk.core.api.auth.dto.request.RefreshRequest;
import com.guincard.penghyunsuk.core.api.auth.dto.request.SignupRequest;
import com.guincard.penghyunsuk.core.api.auth.dto.response.TokenResponse;
import com.guincard.penghyunsuk.core.domain.auth.service.AuthService;
import com.guincard.penghyunsuk.core.domain.auth.service.UserService;
import com.guincard.penghyunsuk.core.support.response.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResult<Long>> signup(@RequestBody @Valid SignupRequest request) {
        Long userId = userService.signup(request);
        return ResponseEntity.ok(ApiResult.success(userId, "회원가입이 완료되었습니다."));
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResult<TokenResponse>> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResult.success(response, "로그인에 성공했습니다."));
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResult<TokenResponse>> refresh(@RequestBody @Valid RefreshRequest request) {
        TokenResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResult.success(response, "토큰이 갱신되었습니다."));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(@RequestBody @Valid RefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResult.success(null, "로그아웃되었습니다."));
    }
}