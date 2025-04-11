package com.bb.webcanvasservice.domain.game.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

/**
 * 랜덤 코드 생성 방식의 조인 코드 생성이 정상적으로 수행되지 않았을 때 발생하는 exception
 */
public class JoinCodeNotGeneratedException extends BusinessException {

    public JoinCodeNotGeneratedException(String message) {
        super(message);
    }
}
