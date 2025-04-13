package com.bb.webcanvasservice.domain.user.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

import static com.bb.webcanvasservice.common.code.ErrorCode.USER_NOT_FOUND;


/**
 * 요청한 쿼리에 대한 유저를 찾지 못했을 때 발생하는 exception
 */
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(USER_NOT_FOUND, USER_NOT_FOUND.getDefaultMessage());
    }

    public UserNotFoundException(String message) {
        super(USER_NOT_FOUND, message);
    }
}
