package com.bb.webcanvasservice.websocket.handler;

import com.bb.webcanvasservice.common.exception.BusinessException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;

/**
 * 웹소켓 MessageMapping 처리 중 발생하는 예외 공통 처리를 위한 클래스
 */
public class WebSocketExceptionHandler {

    /**
     * 사용자 개별 큐로 예외 전송
     */
    @MessageExceptionHandler(BusinessException.class)
    @SendToUser("/queue/errors")
    public String handleBusinessException(BusinessException e) {
        return e.getMessage();
    }
}
