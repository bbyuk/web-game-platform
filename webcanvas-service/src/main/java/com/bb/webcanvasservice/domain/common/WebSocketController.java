package com.bb.webcanvasservice.domain.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트가 "/app/draw"로 보낸 메시지 처리
    @MessageMapping("/draw")
    @SendTo("/topic")
    public String handleDrawMessage(String drawData) {
        log.error("Draw data received: {}", drawData);  // 로그 추가
        return drawData; // 캔버스에 그려진 데이터를 모두 구독자에게 전송
    }

    // 특정 사용자에게 직접 메시지를 전송하는 예시
    public void sendToSpecificUser(Long userId, String message) {
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/notifications", message);
    }
}
