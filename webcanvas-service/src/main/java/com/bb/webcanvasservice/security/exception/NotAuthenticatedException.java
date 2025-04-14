package com.bb.webcanvasservice.security.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;

/**
 * Spring SecurityFilterChain에서 예외처리할 수 있도록 AuthenticationException을 구현한 예외 클래스
 */
public class NotAuthenticatedException extends ApplicationAuthenticationException {

    public NotAuthenticatedException(ErrorCode errorCode) {
        super(errorCode, errorCode.getDefaultMessage());
    }

    public NotAuthenticatedException(String message) {
        super(ErrorCode.INVALID_TOKEN, message);
    }

    public NotAuthenticatedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
