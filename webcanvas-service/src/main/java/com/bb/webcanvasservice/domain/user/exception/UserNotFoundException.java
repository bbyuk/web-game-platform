package com.bb.webcanvasservice.domain.user.exception;

import com.bb.webcanvasservice.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
