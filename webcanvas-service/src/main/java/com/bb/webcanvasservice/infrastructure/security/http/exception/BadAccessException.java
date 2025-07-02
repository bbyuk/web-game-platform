package com.bb.webcanvasservice.infrastructure.security.http.exception;

/**
 * 리소스에 대한 잘못된 접근을 처리하기 위한 Exception
 */
public class BadAccessException extends RuntimeException {
    public BadAccessException(String message) {
        super(message);
    }
}
