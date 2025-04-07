package com.bb.webcanvasservice.domain.game.exception;

import com.bb.webcanvasservice.common.exception.NotFoundException;

/**
 * 게임 방에 입장해 있는 기록을 찾지 못했을 떄 발생하는 exception
 */
public class GameRoomEntranceNotFoundException extends NotFoundException {
    public GameRoomEntranceNotFoundException(String message) {
        super(message);
    }
}
