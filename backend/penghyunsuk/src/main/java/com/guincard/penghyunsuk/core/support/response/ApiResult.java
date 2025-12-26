package com.guincard.penghyunsuk.core.support.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    private boolean success;
    private T data;
    private String message;
    private String errorCode;

    // 성공 응답 (데이터 있음)
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.success = true;
        result.data = data;
        return result;
    }

    // 성공 응답 (데이터 없음)
    public static <T> ApiResult<T> success() {
        ApiResult<T> result = new ApiResult<>();
        result.success = true;
        return result;
    }

    // 성공 응답 (메시지 포함)
    public static <T> ApiResult<T> success(T data, String message) {
        ApiResult<T> result = new ApiResult<>();
        result.success = true;
        result.data = data;
        result.message = message;
        return result;
    }

    // 실패 응답
    public static <T> ApiResult<T> error(String errorCode, String message) {
        ApiResult<T> result = new ApiResult<>();
        result.success = false;
        result.errorCode = errorCode;
        result.message = message;
        return result;
    }
}