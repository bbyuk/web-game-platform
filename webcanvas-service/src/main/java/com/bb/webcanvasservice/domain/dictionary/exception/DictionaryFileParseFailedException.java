package com.bb.webcanvasservice.domain.dictionary.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

public class DictionaryFileParseFailedException extends BusinessException {
    public DictionaryFileParseFailedException() {
        super(ErrorCode.DICTIONARY_FILE_PARSE_FAILED, ErrorCode.DICTIONARY_FILE_PARSE_FAILED.getDefaultMessage());
    }

    public DictionaryFileParseFailedException(String message) {
        super(ErrorCode.DICTIONARY_FILE_PARSE_FAILED, message);
    }
}
