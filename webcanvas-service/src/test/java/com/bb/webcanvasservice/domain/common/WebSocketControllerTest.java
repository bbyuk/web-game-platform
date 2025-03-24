package com.bb.webcanvasservice.domain.common;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)  // MockitoExtension을 사용하여 mock 객체 초기화
class WebSocketControllerTest {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;  // 메시지 전송을 위한 템플릿


    private WebSocketController websocketController;

    @BeforeEach
    public void setUp() {
        websocketController = new WebSocketController(simpMessagingTemplate);
    }

    @Test
    public void testSendMessage() {
        // 메세지
        String message = "Hello message";


        // 웹소켓 세션에서 메시지 전송 메서드 호출 확인
        String result = websocketController.handleDrawMessage(message);

        // mockito를 통해 메시지가 전송된 것을 확인
        Mockito.verify(simpMessagingTemplate, times(1))
                .convertAndSend(eq("/topic/messages"), eq(message));

        // 결과 검증 (메시지가 그대로 반환되어야 함)
        assertEquals(message, result);
    }

}