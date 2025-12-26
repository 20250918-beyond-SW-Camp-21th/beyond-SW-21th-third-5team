package com.guincard.penghyunsuk.core.support.error;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {

    private final ErrorCode errorCode;

    public CoreException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CoreException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}