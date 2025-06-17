package com.bb.webcanvasservice.dictionary.domain.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

public class DictionaryNotSetException extends BusinessException {
    public DictionaryNotSetException() {
        super(ErrorCode.SYSTEM_ERROR, "사전 데이터 설정이 되지 않았습니다.");
    }
}
