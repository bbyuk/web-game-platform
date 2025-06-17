package com.bb.webcanvasservice.game.domain.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

import static com.bb.webcanvasservice.common.code.ErrorCode.GAME_ROOM_ENTRANCE_NOT_FOUND;

/**
 * 게임 방에 입장해 있는 기록을 찾지 못했을 떄 발생하는 exception
 */
public class GameRoomEntranceNotFoundException extends BusinessException {
    public GameRoomEntranceNotFoundException() {
        super(GAME_ROOM_ENTRANCE_NOT_FOUND, GAME_ROOM_ENTRANCE_NOT_FOUND.getDefaultMessage());
    }

    public GameRoomEntranceNotFoundException(String message) {
        super(GAME_ROOM_ENTRANCE_NOT_FOUND, message);
    }

}
