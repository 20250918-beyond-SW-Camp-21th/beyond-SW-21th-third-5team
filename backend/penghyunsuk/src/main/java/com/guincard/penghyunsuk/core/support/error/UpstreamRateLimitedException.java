package com.guincard.penghyunsuk.core.support.error;

import java.time.Duration;

public class UpstreamRateLimitedException extends RuntimeException {

    private final Duration retryAfter;

    public UpstreamRateLimitedException(String message, Duration retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public Duration getRetryAfter() {
        return retryAfter;
    }
}
