package com.guincard.penghyunsuk.core.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "지원하지 않는 HTTP 메서드입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C004", "잘못된 타입입니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "C005", "필수 파라미터가 누락되었습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "이미 사용중인 이메일입니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "U003", "이미 사용중인 아이디입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U004", "비밀번호가 올바르지 않습니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A003", "아이디 또는 비밀번호가 올바르지 않습니다."),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "세션이 만료되었습니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}