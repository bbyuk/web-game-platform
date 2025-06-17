package com.bb.webcanvasservice.common.lock.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

/**
 * 이미 락을 점유하고 실행중인 task에 대해 추가로 요청이 들어온 경우 발생
 */
public class LockAlreadyOccupiedException extends BusinessException {
    public LockAlreadyOccupiedException() {
        super(ErrorCode.BAD_REQUEST, "이미 락이 점유되어 있습니다.");
    }

    public LockAlreadyOccupiedException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
