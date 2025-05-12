package com.bb.webcanvasservice.domain.dictionary.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

public class WordNotFoundException extends BusinessException {
    public WordNotFoundException() {
        super(ErrorCode.WORD_NOT_FOUND, ErrorCode.WORD_NOT_FOUND.getDefaultMessage());
    }
}
