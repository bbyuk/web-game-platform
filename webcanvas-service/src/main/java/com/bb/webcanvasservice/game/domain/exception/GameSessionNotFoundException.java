package com.bb.webcanvasservice.game.domain.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

import static com.bb.webcanvasservice.common.code.ErrorCode.GAME_SESSION_NOT_FOUND;

public class GameSessionNotFoundException extends BusinessException {
    public GameSessionNotFoundException() {
        super(GAME_SESSION_NOT_FOUND, GAME_SESSION_NOT_FOUND.getDefaultMessage());
    }
}
