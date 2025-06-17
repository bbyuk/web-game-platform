package com.bb.webcanvasservice.infrastructure.security.web.authorization;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.infrastructure.web.dto.ExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Spring Security Web 인가 실패에 대한 공통 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * Security 관련 에러 코드의 메세지는 모호하게 처리
     */
    private static final String ACCESS_DENIED_MESSAGE = "접근 권한이 없습니다.";

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
        log.error("Failed to authorize.");
        log.error("ErrorCode = {}", ErrorCode.ACCESS_DENIED.getCode());
        log.error(accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter()
                .write(
                        objectMapper.writeValueAsString(
                                new ExceptionResponse(
                                        LocalDateTime.now(),
                                        ErrorCode.ACCESS_DENIED.getCode(), // 권한 없음에 대한 예외는 단일 케이스
                                        ACCESS_DENIED_MESSAGE,
                                        request.getRequestURI()
                                )
                        )
                );
    }
}
