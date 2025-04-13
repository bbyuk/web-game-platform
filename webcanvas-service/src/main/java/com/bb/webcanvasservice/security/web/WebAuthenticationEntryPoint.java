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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Spring Security Web 인증 실패에 대한 공통 처리
 */
@RequiredArgsConstructor
public class WebAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Spring SecurityFilterChain 내에서 인증에 성공하지 못하고 막힌 경우 401 Unauthorized 코드와 함께 공통 스펙으로 응답 처리를 담당.
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorCode errorCode = (authException instanceof ApplicationAuthenticationException)
                ? ((ApplicationAuthenticationException) authException).getErrorCode()
                : ErrorCode.INVALID_ACCESS_TOKEN;

        response.getWriter()
                .write(
                        objectMapper.writeValueAsString(
                                new ExceptionResponse(
                                        LocalDateTime.now(),
                                        errorCode.getCode(),
                                        authException.getMessage(),
                                        request.getRequestURI()
                                )
                        )
                );
    }
}
