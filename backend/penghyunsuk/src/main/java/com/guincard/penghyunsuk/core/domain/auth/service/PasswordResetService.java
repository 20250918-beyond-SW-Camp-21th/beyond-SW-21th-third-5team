package com.guincard.penghyunsuk.core.domain.auth.service;


import com.guincard.penghyunsuk.core.api.auth.dto.request.PasswordResetConfirmRequest;
import com.guincard.penghyunsuk.core.api.auth.dto.request.PasswordResetRequest;
import com.guincard.penghyunsuk.core.domain.auth.Repository.PasswordResetTokenRepository;
import com.guincard.penghyunsuk.core.domain.auth.Repository.RefreshTokenRepository;
import com.guincard.penghyunsuk.core.domain.auth.Repository.UserRepository;
import com.guincard.penghyunsuk.core.domain.auth.entity.PasswordResetToken;
import com.guincard.penghyunsuk.core.domain.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    // (선택) 리프레시 토큰을 DB에 저장한다면 넣고, 아니면 이 필드/라인 삭제
    private final RefreshTokenRepository refreshTokenRepository;

    private static final Duration TTL = Duration.ofMinutes(10);

    // 코드 발급
    @Transactional
    public void requestReset(PasswordResetRequest req) {
        if (req == null) return;

        String loginId = trimToNull(req.getLoginId());
        String email = trimToNull(req.getEmail());
        if (loginId == null || email == null) return;

        Optional<User> optUser = userRepository.findByLoginId(loginId);
        if (optUser.isEmpty()) return;

        User user = optUser.get();

        String savedEmail = trimToNull(user.getEmail());
        if (savedEmail == null || !savedEmail.equalsIgnoreCase(email)) return;

        // 6자리 코드 생성
        String code = generate6DigitCode();

        // 토큰 저장(해시 저장 권장)
        String codeHash = sha256(code);

        PasswordResetToken token = PasswordResetToken.create(
                user.getId(), codeHash, TTL
        );
        tokenRepository.save(token);

        // 메일 발송 (메일 템플릿 1개만 사용)
        mailService.sendPasswordResetCode(user.getEmail(), code);
    }

    // 코드 검증
    @Transactional
    public void confirmReset(PasswordResetConfirmRequest req) {
        if (req == null) throw new IllegalArgumentException("잘못된 요청입니다.");

        String loginId = trimToNull(req.getLoginId());
        String code = trimToNull(req.getCode());
        String newPassword = trimToNull(req.getNewPassword());

        if (loginId == null || code == null || newPassword == null) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("코드가 만료되었거나 올바르지 않습니다."));

        // 가장 최근 토큰 1개를 검증(원하면 userId+codeHash로 찾는 방식으로 바꿔도 됨)
        PasswordResetToken token = tokenRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("코드가 만료되었거나 올바르지 않습니다."));

        if (token.isUsed() || token.isExpired()) {
            throw new IllegalArgumentException("코드가 만료되었거나 올바르지 않습니다.");
        }

        String inputHash = sha256(code);
        if (!token.getCodeHash().equals(inputHash)) {
            throw new IllegalArgumentException("코드가 만료되었거나 올바르지 않습니다.");
        }

        // 비밀번호 변경 (User 엔티티에 맞게 setPassword / changePassword 중 맞는 걸로)
        user.updatePassword(passwordEncoder.encode(newPassword));

        // 1회용 처리
        token.markUsed();

        // (선택) 비번 바꾸면 기존 세션/토큰 무효화: refresh 토큰 전부 삭제 권장
        refreshTokenRepository.deleteByLoginId(user.getLoginId());
    }

    // ----------------- helpers -----------------

    private String generate6DigitCode() {
        int n = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%06d", n);
    }

    private String sha256(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
