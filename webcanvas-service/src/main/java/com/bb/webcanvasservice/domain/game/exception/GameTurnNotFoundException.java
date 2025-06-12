package com.bb.webcanvasservice.domain.game.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

/**
 * 게임 턴을 찾지 못했을 때 발생하는 예외
 */
public class GameTurnNotFoundException extends BusinessException {
    public GameTurnNotFoundException() {
        super(ErrorCode.GAME_TURN_NOT_FOUND, ErrorCode.GAME_TURN_NOT_FOUND.getDefaultMessage());
    }
}
