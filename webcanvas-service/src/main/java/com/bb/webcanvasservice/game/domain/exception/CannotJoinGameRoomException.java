package com.bb.webcanvasservice.game.domain.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

/**
 * 게임 방에 입장할 수 없는 상태일 때 발생하는 비즈니스 예외
 */
public class CannotJoinGameRoomException extends BusinessException {

    public CannotJoinGameRoomException() {
        super(ErrorCode.CAN_NOT_JOIN_GAME_ROOM, ErrorCode.CAN_NOT_JOIN_GAME_ROOM.getDefaultMessage());
    }
}
