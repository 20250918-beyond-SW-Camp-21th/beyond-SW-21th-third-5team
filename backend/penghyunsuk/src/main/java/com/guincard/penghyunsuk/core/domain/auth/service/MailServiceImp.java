package com.guincard.penghyunsuk.core.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailServiceImp implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MailProperties mailProperties;


    private String getFromAddress() {
        return mailProperties.getUsername();
    }

    private void sendHtmlMail(String to, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process(templateName, context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                            StandardCharsets.UTF_8.name());

            helper.setFrom(getFromAddress());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("메일 전송 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public void sendPasswordResetCode(String to, String code) {
        String subject = "[guincard] 비밀번호 재설정 코드 안내";

        Map<String, Object> vars = new HashMap<>();
        // 템플릿에서 ${code} 로 쓰게 권장
        vars.put("code", code);

        // resources/templates/mail/ResetPassword.html
        sendHtmlMail(to, subject, "mail/ResetPassword", vars);
    }
}
