package com.guincard.penghyunsuk.core.support.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guincard.penghyunsuk.core.support.error.ErrorCode;
import com.guincard.penghyunsuk.core.support.response.ApiResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인가 실패 시 처리 (403 Forbidden) - 현재는 사용 안 함
 * 관리자 권한 등이 추가되면 활성화
 */
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ApiResult<Void> errorResponse = ApiResult.error(
                "A002",
                "접근 권한이 없습니다."
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}