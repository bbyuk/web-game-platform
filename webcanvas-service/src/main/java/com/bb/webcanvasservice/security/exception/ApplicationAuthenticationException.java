package com.bb.webcanvasservice.security.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * 애플리케이션에 의해 처리된 인증/인가 관련 예외
 */
@Getter
public class ApplicationAuthenticationException extends AuthenticationException {

    private final ErrorCode errorCode;

    public ApplicationAuthenticationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
