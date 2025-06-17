package com.bb.webcanvasservice.user.domain.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

import static com.bb.webcanvasservice.common.code.ErrorCode.USER_ALREADY_REGISTERED;

/**
 * 이미 등록된 Fingerprint로 유저 등록을 시도하는 경우 발생하는 Exception
 */
public class AlreadyRegisteredUserException extends BusinessException {

    public AlreadyRegisteredUserException() {
        super(USER_ALREADY_REGISTERED, USER_ALREADY_REGISTERED.getDefaultMessage());
    }

    public AlreadyRegisteredUserException(String message) {
        super(USER_ALREADY_REGISTERED, message);
    }
}
