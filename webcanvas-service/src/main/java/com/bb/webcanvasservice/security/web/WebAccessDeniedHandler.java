package com.bb.webcanvasservice.security.web;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.dto.ExceptionResponse;
import com.bb.webcanvasservice.security.exception.ApplicationAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Spring Security Web 인가 실패에 대한 공통 처리
 */
@RequiredArgsConstructor
public class WebAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * Spring SecurityFilterChain 내에서 인가에 성공하지 못하고 막힌 경우 403 Forbidden 코드와 함께 공통 스펙으로 응답
     *
     * @param request httpServletRequest 요청 객체
     * @param response httpServletResponse 응답 객체
     * @param accessDeniedException 권한 없음 exception
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter()
                .write(
                        objectMapper.writeValueAsString(
                                new ExceptionResponse(
                                        LocalDateTime.now(),
                                        ErrorCode.ACCESS_DENIED.getCode(), // 권한 없음에 대한 예외는 단일 케이스
                                        accessDeniedException.getMessage(),
                                        request.getRequestURI()
                                )
                        )
                );
    }
}
