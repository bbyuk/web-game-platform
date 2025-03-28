package com.bb.webcanvasservice.integration.domain.canvas;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CanvasWebSocketControllerTest {
    @LocalServerPort
    private int port;
    private String WEBSOCKET_URL;
    private final String CANVAS_SUBSCRIBE_TOPIC = "/canvas";
    private final String SEND_DESTINATION = "/draw/stroke";
    private WebSocketStompClient stompClient;

    void setup() {
        WEBSOCKET_URL = String.format("ws://localhost:%s/ws", port);
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }
}