package com.bb.webcanvasservice.domain.game.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;

/**
 * 게임 방의 입장을 요청했으나, 이미 게임 방에 입장해 있는 기록이 있는 경우 발생하는 exception
 */
public class AlreadyEnteredRoomException extends BusinessException {
    public AlreadyEnteredRoomException(String message) {
        super(message);
    }
}
