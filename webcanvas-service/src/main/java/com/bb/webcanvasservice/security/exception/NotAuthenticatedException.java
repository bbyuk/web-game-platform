package com.bb.webcanvasservice.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Spring SecurityFilterChain에서 예외처리할 수 있도록 AuthenticationException을 구현한 예외 클래스
 */
public class NotAuthenticatedException extends AuthenticationException {
    public NotAuthenticatedException(String message) {
        super(message);
    }
}
