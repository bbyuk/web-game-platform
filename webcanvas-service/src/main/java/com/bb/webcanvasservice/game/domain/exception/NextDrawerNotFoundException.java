package com.bb.webcanvasservice.game.domain.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

/**
 * 다음 턴에 그림을 그릴 유저를 찾을 수 없을 떄 발생
 */
public class NextDrawerNotFoundException extends BusinessException {

    public NextDrawerNotFoundException() {
        super(ErrorCode.SYSTEM_ERROR, "그림을 그릴 유저를 찾지 못했습니다. 시스템 관리자에게 문의해주세요.");
    }
}
