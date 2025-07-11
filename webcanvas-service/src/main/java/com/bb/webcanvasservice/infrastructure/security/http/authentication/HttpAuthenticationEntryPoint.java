package com.bb.webcanvasservice.infrastructure.security.http.authentication;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.infrastructure.exception.model.http.ExceptionResponse;
import com.bb.webcanvasservice.infrastructure.security.http.exception.ApplicationAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Spring Security Web 인증 실패에 대한 공통 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private static final String UNAUTHORIZED_MESSAGE = "인증에 실패했습니다.";

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
        ErrorCode errorCode = (authException instanceof ApplicationAuthenticationException)
                ? ((ApplicationAuthenticationException) authException).getErrorCode()
                : ErrorCode.UNAUTHORIZED;

        log.error("Failed to authentication.");
        log.error("ErrorCode = {}", errorCode);
        log.error(authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");


        response.getWriter()
                .write(
                        objectMapper.writeValueAsString(
                                new ExceptionResponse(
                                        LocalDateTime.now(),
                                        errorCode.getCode(),
                                        UNAUTHORIZED_MESSAGE,
                                        request.getRequestURI()
                                )
                        )
                );
    }
}
