package com.bb.webcanvasservice.domain.user.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 이미 등록된 Fingerprint로 유저 등록을 시도하는 경우 발생하는 Exception
 */
public class AlreadyRegisteredUserException extends BusinessException {

    public AlreadyRegisteredUserException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
