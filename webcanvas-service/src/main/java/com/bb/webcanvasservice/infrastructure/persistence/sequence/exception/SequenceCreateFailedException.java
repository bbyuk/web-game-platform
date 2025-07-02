package com.bb.webcanvasservice.infrastructure.persistence.sequence.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

public class SequenceCreateFailedException extends BusinessException {

    public SequenceCreateFailedException() {
        super(ErrorCode.SYSTEM_ERROR, ErrorCode.SYSTEM_ERROR.getDefaultMessage());
    }
}
