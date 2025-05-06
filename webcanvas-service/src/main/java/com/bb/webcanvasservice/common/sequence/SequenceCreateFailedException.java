package com.bb.webcanvasservice.common.sequence;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

public class SequenceCreateFailedException extends BusinessException {

    public SequenceCreateFailedException() {
        super(ErrorCode.SYSTEM_ERROR, ErrorCode.SYSTEM_ERROR.getDefaultMessage());
    }
}
