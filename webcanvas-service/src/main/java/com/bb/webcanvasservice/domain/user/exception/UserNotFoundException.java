package com.bb.webcanvasservice.domain.user.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;
import org.springframework.http.HttpStatus;


/**
 * 요청한 쿼리에 대한 유저를 찾지 못했을 때 발생하는 exception
 */
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
