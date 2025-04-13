package com.bb.webcanvasservice.domain.game.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

import static com.bb.webcanvasservice.common.code.ErrorCode.GAME_ROOM_HAS_ILLEGAL_STATUS;

/**
 * 현재 게임 방의 상태가 게임 방에 대한 요청을 받을 수 없는 상태일 때 발생하는 exception
 */
public class IllegalGameRoomStateException extends BusinessException {
    public IllegalGameRoomStateException() {
        super(GAME_ROOM_HAS_ILLEGAL_STATUS, GAME_ROOM_HAS_ILLEGAL_STATUS.getDefaultMessage());
    }
    public IllegalGameRoomStateException(String message) {
        super(GAME_ROOM_HAS_ILLEGAL_STATUS, message);
    }

}
