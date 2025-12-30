package com.guincard.penghyunsuk.core.domain.auth.service;

public interface MailService {
    void sendPasswordResetCode(String to, String code);
}

