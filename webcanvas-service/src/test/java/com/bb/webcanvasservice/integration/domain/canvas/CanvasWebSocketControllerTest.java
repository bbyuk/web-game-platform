package com.bb.webcanvasservice.integration.domain.canvas;

import com.bb.webcanvasservice.domain.canvas.dto.Point;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CanvasWebSocketControllerTest {
    @LocalServerPort
    private int port;
    private String WEBSOCKET_URL;
    private final String CANVAS_SUBSCRIBE_TOPIC = "/canvas";
    private final String SEND_DESTINATION = "/draw/stroke";
    private WebSocketStompClient stompClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        WEBSOCKET_URL = String.format("ws://localhost:%s/canvas", port);
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    @DisplayName("웹 소켓 접속 및 stroke 처리 - canvas 웹 소켓에 연결하고 stroke 이벤트 send시 구독중인 클라이언트로 stroe 메세지를 전송한다.")
    void testWebSocketDrawMessage() throws Exception {
        // given

        // WebSocket 연결 설정
        StompSession session = stompClient.connectAsync(WEBSOCKET_URL, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get();

        // 구독
        CompletableFuture<Stroke> subscribeFuture = new CompletableFuture<>();
        session.subscribe(String.format("%s/%d", CANVAS_SUBSCRIBE_TOPIC, 1L), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Object.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    subscribeFuture.complete(objectMapper.readValue((byte[]) payload, Stroke.class));
                }
                catch(IOException e) {
                    System.out.println("e = " + e);
                    subscribeFuture.completeExceptionally(e);
                }
            }
        });

        Stroke testStroke = Stroke.builder()
                .gameRoomId(1L)
                .userId(100L)
                .color("FF5733")  // 예제 색상 (주황빛 빨강)
                .lineWidth(5)
                .points(List.of(
                        Point.builder()
                                .x(10)
                                .y(20)
                                .build(),
                        Point.builder()
                                .x(20)
                                .y(30)
                                .build(),
                        Point.builder()
                                .x(30)
                                .y(40)
                                .build()
                ))
                .build();


        // when
        session.send(SEND_DESTINATION, testStroke);

        // then
        Stroke result = subscribeFuture.get(3, TimeUnit.SECONDS);

        Assertions.assertThat(result.getGameRoomId()).isEqualTo(testStroke.getGameRoomId());
        Assertions.assertThat(result.getUserId()).isEqualTo(testStroke.getUserId());
        Assertions.assertThat(result.getLineWidth()).isEqualTo(testStroke.getLineWidth());
        Assertions.assertThat(result.getPoints().size()).isEqualTo(testStroke.getPoints().size());
    }

}