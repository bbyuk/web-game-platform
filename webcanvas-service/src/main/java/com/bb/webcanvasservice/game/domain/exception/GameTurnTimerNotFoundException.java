package com.bb.webcanvasservice.game.domain.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

/**
 * 게임 턴 타이머를 찾지 못했을 때 발생하는 예외
 */
public class GameTurnTimerNotFoundException extends BusinessException {

    public GameTurnTimerNotFoundException() {
        super(ErrorCode.SYSTEM_ERROR, "게임 턴 타이머를 찾지 못했습니다.");
    }
}
