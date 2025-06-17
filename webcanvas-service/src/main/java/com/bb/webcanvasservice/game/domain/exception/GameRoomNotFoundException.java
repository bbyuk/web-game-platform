package com.bb.webcanvasservice.game.domain.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

import static com.bb.webcanvasservice.common.code.ErrorCode.GAME_ROOM_NOT_FOUND;

/**
 * 요청받은 쿼리에 해당하는 게임 방을 찾지 못했을 때 발생하는 exception
 */
public class GameRoomNotFoundException extends BusinessException {

    public GameRoomNotFoundException() {
        super(GAME_ROOM_NOT_FOUND, GAME_ROOM_NOT_FOUND.getDefaultMessage());
    }

    public GameRoomNotFoundException(String message) {
        super(GAME_ROOM_NOT_FOUND, message);
    }
}
