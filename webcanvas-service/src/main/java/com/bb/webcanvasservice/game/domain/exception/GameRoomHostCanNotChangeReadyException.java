package com.bb.webcanvasservice.game.domain.exception;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;

/**
 * 게임 방 Host는 ready 상태를 변경할 수 없다.
 */
public class GameRoomHostCanNotChangeReadyException extends BusinessException {

    public GameRoomHostCanNotChangeReadyException() {
        super(ErrorCode.BAD_REQUEST, "호스트는 레디 상태를 변경할 수 없습니다.");
    }
}
