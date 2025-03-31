package com.bb.webcanvasservice.integration.domain.canvas;

import com.bb.webcanvasservice.domain.canvas.dto.Point;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.domain.game.GameService;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
import com.bb.webcanvasservice.security.JwtManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CanvasWebSocketControllerTest {
    @LocalServerPort
    private int port;
    private String WEBSOCKET_URL;
    private final String CANVAS_SUBSCRIBE_TOPIC = "/canvas";
    private final String SEND_DESTINATION = "/draw/stroke";

    /**
     * WebSocket 연결 client
     */
    private StompHeaders headers;
    private StompSession session;
    private WebSocketStompClient stompClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * testUserId
     */
    private Long testUserId;
    private User testUser;
    private final Stroke testStroke = Stroke.builder()
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

    /**
     * 테스트에 필요한 bean 목록
     */
    @Autowired
    private JwtManager jwtManager;
    @Autowired
    private GameService gameService;
    @Autowired
    private UserRepository userRepository;

    /**
     * 테스트 클라이언트 게임 방 구독 메소드
     * @param gameRoomId
     * @return
     */
    private CompletableFuture<Stroke> subscribeRoomCanvas(Long gameRoomId) {
        CompletableFuture<Stroke> subscribeFuture = new CompletableFuture<>();
        session.subscribe(String.format("%s/%d", CANVAS_SUBSCRIBE_TOPIC, gameRoomId), new StompFrameHandler() {
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
        return subscribeFuture;
    }


    @BeforeEach
    void setup() throws Exception {
        WEBSOCKET_URL = String.format("ws://localhost:%s/canvas", port);
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        /**
         * test 유저 생성
         */
        testUser = new User(UUID.randomUUID().toString());
        testUserId = userRepository.save(testUser).getId();


        // 소켓 connect 헤더
        // 웹소켓 연결 이전에 platform-service 로부터 토큰 발급받음.
        headers = new StompHeaders();
        headers.add(JwtManager.BEARER_TOKEN, String.format("%s %s", JwtManager.TOKEN_PREFIX, jwtManager.generateToken(testUserId)));

        // WebSocket session 연결
        session = stompClient.connectAsync(WEBSOCKET_URL, new WebSocketHttpHeaders(), headers, new StompSessionHandlerAdapter() {}).get();

    }

    @Test
    @DisplayName("웹 소켓 접속 및 stroke 처리 - canvas 웹 소켓에 연결하고 stroke 이벤트 send시 같은 방에 입장해 웹 소켓에 연결되어 있는 클라이언트로 stroke 메세지를 전송한다.")
    void testWebSocketDrawMessage() throws Exception {
        // given

        // 방에 접속해 있어야한다.
        Long gameRoomId = gameService.createGameRoomAndEnter(testUserId);


        // 구독
        CompletableFuture<Stroke> subscribeFuture = subscribeRoomCanvas(gameRoomId);


        // when
        session.send(SEND_DESTINATION, testStroke);

        // then
        Stroke result = subscribeFuture.get(3, TimeUnit.SECONDS);

        Assertions.assertThat(result.getLineWidth()).isEqualTo(testStroke.getLineWidth());
        Assertions.assertThat(result.getPoints().size()).isEqualTo(testStroke.getPoints().size());
    }


    @Test
    @DisplayName("웹 소켓 접속 및 strke 처리 - 같은 방에 입장한 클라이언트는 stroke 이벤트를 받아볼 수 있다.")
    void testWebSocketDrawMessageAtOtherClients() {

    }

}