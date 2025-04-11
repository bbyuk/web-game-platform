package com.bb.webcanvasservice.domain.game.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 현재 게임 방의 상태가 게임 방에 대한 요청을 받을 수 없는 상태일 때 발생하는 exception
 */
public class IllegalGameRoomStateException extends BusinessException {
    public IllegalGameRoomStateException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
