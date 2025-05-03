package com.bb.webcanvasservice.domain.dictionary.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

import static com.bb.webcanvasservice.common.code.ErrorCode.DICTIONARY_FILE_DOWNLOAD_FAILED;

public class DictionaryFileDownloadFailedException extends BusinessException {
    public DictionaryFileDownloadFailedException() {
        super(DICTIONARY_FILE_DOWNLOAD_FAILED, DICTIONARY_FILE_DOWNLOAD_FAILED.getDefaultMessage());
    }

    public DictionaryFileDownloadFailedException(String message) {
        super(ErrorCode.DICTIONARY_FILE_DOWNLOAD_FAILED, message);
    }
}
