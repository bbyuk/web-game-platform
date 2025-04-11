package com.bb.webcanvasservice.domain.game.exception;

import com.bb.webcanvasservice.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 요청받은 쿼리에 해당하는 게임 방을 찾지 못했을 때 발생하는 exception
 */
public class GameRoomNotFoundException extends BusinessException {
    public GameRoomNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
