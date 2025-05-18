package com.bb.webcanvasservice.websocket.handler;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.common.exception.BusinessException;
import com.bb.webcanvasservice.websocket.exception.AbnormalWebSocketAccessException;
import com.bb.webcanvasservice.websocket.security.AbnormalAccessRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 웹소켓 MessageMapping 처리 중 발생하는 예외 공통 처리를 위한 클래스
 */
@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    private final AbnormalAccessRegistry abnormalAccessRegistry;

    /**
     * 사용자 개별 큐로 예외 전송
     */
    @MessageExceptionHandler(BusinessException.class)
    @SendToUser("/queue/errors")
    public String handleBusinessException(BusinessException e) {
        return e.getMessage();
    }

    /**
     * 악성 접근 수집
     */
    @MessageExceptionHandler(AbnormalWebSocketAccessException.class)
    @SendToUser("/queue/errors")
    public String handleAbnormalAccessException(AbnormalWebSocketAccessException e) {
        abnormalAccessRegistry.add(e.getIpAddress());
        return e.getMessage();
    }

}
