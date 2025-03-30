package com.bb.webcanvasservice.security.exception;

import org.springframework.security.core.AuthenticationException;

public class NotAuthenticatedException extends AuthenticationException {
    public NotAuthenticatedException(String message) {
        super(message);
    }
}
