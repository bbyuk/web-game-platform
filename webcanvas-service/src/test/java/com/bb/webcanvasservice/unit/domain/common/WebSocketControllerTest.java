package com.bb.webcanvasservice.unit.domain.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketControllerTest {

    @LocalServerPort
    private int port;
    private String WEBSOCKET_URL;
    private final String SUBSCRIBE_TOPIC = "/topic";
    private final String SEND_DESTINATION = "/app/draw";

    private WebSocketStompClient stompClient;

//    @BeforeEach
    void setup() {
        WEBSOCKET_URL = String.format("ws://localhost:%s/ws", port);
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

//    @Test
    void testWebSocketDrawMessage() throws Exception {
        // WebSocket 연결 설정
        StompSession session = stompClient.connectAsync(
                WEBSOCKET_URL,
                new WebSocketHttpHeaders(),
                new StompSessionHandlerAdapter() {}
        ).get(3, TimeUnit.SECONDS);

        // /topic/canvas 구독
        CompletableFuture<String> subscribeFuture = new CompletableFuture<>();
        session.subscribe(SUBSCRIBE_TOPIC, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                subscribeFuture.complete(payload.toString());
            }
        });

        // WebSocket 메세지 전송
        String testMessage = "{\"x\":100, \"y\": 200}";
        session.send(SEND_DESTINATION, testMessage);


//        // 결과 검증
        String receivedMessage = subscribeFuture.get(60, TimeUnit.SECONDS);
        Assertions.assertThat(receivedMessage).isEqualTo(testMessage);

    }
}