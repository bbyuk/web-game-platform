package com.bb.webcanvasservice.domain.game.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

import static com.bb.webcanvasservice.common.code.ErrorCode.USER_ALREADY_ENTERED_GAME_ROOM;

/**
 * 게임 방의 입장을 요청했으나, 이미 게임 방에 입장해 있는 기록이 있는 경우 발생하는 exception
 */
public class AlreadyEnteredRoomException extends BusinessException {
    public AlreadyEnteredRoomException() {
        super(USER_ALREADY_ENTERED_GAME_ROOM, USER_ALREADY_ENTERED_GAME_ROOM.getDefaultMessage());
    }

    public AlreadyEnteredRoomException(String message) {
        super(USER_ALREADY_ENTERED_GAME_ROOM, message);
    }


}
