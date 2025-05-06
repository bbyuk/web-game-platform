package com.bb.webcanvasservice.common.sequence;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

public class SequenceNotFoundException extends BusinessException {
    SequenceNotFoundException() {
        super(ErrorCode.SYSTEM_ERROR, ErrorCode.SYSTEM_ERROR.getDefaultMessage());
    }
}
