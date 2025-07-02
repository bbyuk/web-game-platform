package com.bb.webcanvasservice.infrastructure.messaging.websocket;

import com.bb.webcanvasservice.common.messaging.websocket.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 인프라스트럭쳐 레이어의 웹소켓 메세지 전송 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class WebSocketMessageSender implements MessageSender {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void send(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }
}
