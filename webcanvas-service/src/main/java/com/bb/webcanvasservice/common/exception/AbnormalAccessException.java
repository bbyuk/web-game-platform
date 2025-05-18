package com.bb.webcanvasservice.common.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;

/**
 * 비정상적인 접근일 시 발생하는 예외로 추후 로깅 처리 및 밴 처리 추가
 * TODO 로깅처리 추가
 */
public class AbnormalAccessException extends BusinessException {

    public AbnormalAccessException() {
        super(ErrorCode.ABNORMAL_ACCESS, ErrorCode.ABNORMAL_ACCESS.getDefaultMessage());
    }
}
