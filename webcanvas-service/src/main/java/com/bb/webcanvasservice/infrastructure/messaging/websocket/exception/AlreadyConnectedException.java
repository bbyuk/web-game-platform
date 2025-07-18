package com.bb.webcanvasservice.infrastructure.messaging.websocket.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

import static com.bb.webcanvasservice.common.code.ErrorCode.USER_ALREADY_ENTERED_GAME_ROOM;

public class AlreadyConnectedException extends BusinessException {

    public AlreadyConnectedException() {
        super(USER_ALREADY_ENTERED_GAME_ROOM, USER_ALREADY_ENTERED_GAME_ROOM.getDefaultMessage());
    }
}
