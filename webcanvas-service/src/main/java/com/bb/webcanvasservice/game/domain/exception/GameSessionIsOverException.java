package com.bb.webcanvasservice.game.domain.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

import static com.bb.webcanvasservice.common.code.ErrorCode.GAME_SESSION_IS_OVER;

public class GameSessionIsOverException extends BusinessException {

    public GameSessionIsOverException() {
        super(GAME_SESSION_IS_OVER, GAME_SESSION_IS_OVER.getDefaultMessage());
    }
}
