package com.bb.webcanvasservice.integration.domain.canvas;

import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.domain.game.GameRoomService;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
import com.bb.webcanvasservice.security.auth.JwtManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Transactional
@ActiveProfiles("canvas-integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CanvasWebSocketControllerTest {

    /**
     * 브로드캐스팅 타임아웃
     * 브로드캐스팅 시간 테스트
     */
    private final int BROADCASTING_TIMEOUT = 200;


    @LocalServerPort
    private int port;
    private String WEBSOCKET_URL;
    private final String CANVAS_SUBSCRIBE_TOPIC = "/canvas";
    private final String SEND_DESTINATION = "/draw/stroke";

    /**
     * WebSocket 연결 client
     */
    private StompSession testUserSession;
    private WebSocketStompClient testUserClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 클라이언트에서 유지하고 있어야 하는 유저 JWT
     */
    private String testUserJwt;

    /**
     * 테스트에 필요한 bean 목록
     */
    @Autowired
    private JwtManager jwtManager;
    @Autowired
    private GameRoomService gameRoomService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CanvasTestDataLoader testDataLoader;


    /**
     * 테스트 클라이언트 게임 방 구독 메소드
     * @param gameRoomId
     * @return
     */
    private CompletableFuture<Stroke> subscribeRoomCanvas(StompSession session, Long gameRoomId) {
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

    /**
     * 테스트 클라이언트용 메소드
     * 테스트 클라이언트를 생성해 리턴한다.
     * @return
     */
    private WebSocketStompClient createClient() {
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return webSocketStompClient;
    }

    /**
     * 테스트 클라이언트용 메소드
     * 테스트 클라이언트로 웹소켓 서버에 연결한 세션을 리턴한다.
     * @param client
     * @param jwt
     * @return
     * @throws Exception
     */
    private StompSession connect(WebSocketStompClient client, String jwt) throws Exception {
        // 소켓 connect 헤더
        // 웹소켓 연결 이전에 platform-service 로부터 토큰 발급받음.
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(JwtManager.BEARER_TOKEN, String.format("%s %s", JwtManager.TOKEN_PREFIX, jwt));
        return client.connectAsync(WEBSOCKET_URL, new WebSocketHttpHeaders(), stompHeaders, new StompSessionHandlerAdapter() {}).get();
    }

    @BeforeEach
    void setup() throws Exception {
        WEBSOCKET_URL = String.format("ws://localhost:%s/canvas", port);

        /**
         * test 유저 생성
         * CanvasTestDataLoader 클래스로 테스트 데이터 이동
         */

        /**
         * WebSocket client 생성
         */
        testUserClient = createClient();

        /**
         * WebSocket Session 연결
         */
        testUserJwt = jwtManager.generateToken(testDataLoader.testUser1.getId(), testDataLoader.testUser1.getFingerprint());
        testUserSession = connect(testUserClient, testUserJwt);
    }

    @Test
    @DisplayName("웹 소켓 접속 및 stroke 처리 - canvas 웹 소켓에 연결하고 stroke 이벤트 send시 같은 방에 입장해 웹 소켓에 연결되어 있는 클라이언트로 stroke 메세지를 전송한다.")
    void testWebSocketDrawMessage() throws Exception {
        // given
        // 방에 접속해 있어야한다.

        // 구독
        CompletableFuture<Stroke> subscribeFuture = subscribeRoomCanvas(this.testUserSession, testDataLoader.testGameRoom.getId());

        // when
        Stroke testStroke = testDataLoader.testStroke;

        testUserSession.send(SEND_DESTINATION, testStroke);

        // then
        Stroke result = subscribeFuture.get(BROADCASTING_TIMEOUT, TimeUnit.MILLISECONDS);

        Assertions.assertThat(result.getLineWidth()).isEqualTo(testStroke.getLineWidth());
        Assertions.assertThat(result.getPoints().size()).isEqualTo(testStroke.getPoints().size());
    }


    @Test
    @DisplayName("웹 소켓 접속 및 strke 처리 - 같은 방에 입장한 클라이언트는 stroke 이벤트를 받아볼 수 있다.")
    void testWebSocketDrawMessageAtOtherClients() throws Exception {
        // given

        /**
         *  서버측 게임 방 접속 시나리오 로직
         */
        User otherUser1 = userRepository.save(new User(UUID.randomUUID().toString()));
        gameRoomService.enterGameRoom(testDataLoader.testGameRoom.getId(), otherUser1.getId());

        User otherUser2 = userRepository.save(new User(UUID.randomUUID().toString()));
        gameRoomService.enterGameRoom(testDataLoader.testGameRoom.getId(), otherUser2.getId());

        /**
         * 클라이언트에서 처리해야 할 웹소켓 접속 시나리오 로직
         */
        WebSocketStompClient otherUser1Client = createClient();
        String otherUser1Jwt = jwtManager.generateToken(otherUser1.getId(), otherUser1.getFingerprint());
        StompSession otherUser1Session = connect(otherUser1Client, otherUser1Jwt);
        CompletableFuture<Stroke> otherUser1CompletableFuture = subscribeRoomCanvas(otherUser1Session, testDataLoader.testGameRoom.getId());

        WebSocketStompClient otherUser2Client = createClient();
        String otherUser2Jwt = jwtManager.generateToken(otherUser2.getId(), otherUser2.getFingerprint());
        StompSession otherUser2Session = connect(otherUser2Client, otherUser2Jwt);
        CompletableFuture<Stroke> otherUser2CompletableFuture = subscribeRoomCanvas(otherUser2Session, testDataLoader.testGameRoom.getId());


        /**
         * 현재까지 테스트 유저를 포함해 otherUser1, otherUser2까지 총 3명의 유저가 gameRoomId에 해당하는 게임 방에 입장해있는 상태
         */

        // when
        StompHeaders messageHeader = new StompHeaders();
        messageHeader.add(JwtManager.BEARER_TOKEN, String.format("%s %s", JwtManager.TOKEN_PREFIX, testUserJwt));
        messageHeader.setDestination(SEND_DESTINATION);

        testUserSession.send(messageHeader, testDataLoader.testStroke);

        // then
        Stroke otherUser1ReceivedStroke = otherUser1CompletableFuture.get(BROADCASTING_TIMEOUT, TimeUnit.MILLISECONDS);
        Stroke otherUser2ReceivedStroke = otherUser2CompletableFuture.get(BROADCASTING_TIMEOUT, TimeUnit.MILLISECONDS);


        /**
         * 테스트 유저가 보낸 testStroke - otherUser1이 받은 메세지 비교
         */
        Assertions.assertThat(testDataLoader.testStroke).usingRecursiveComparison().isEqualTo(otherUser1ReceivedStroke);

        /**
         * otherUser1이 받은 메세지 - otherUser2가 받은 메세지 비교 - 동일해야 한다.
         */
        Assertions.assertThat(otherUser1ReceivedStroke).usingRecursiveComparison().isEqualTo(otherUser2ReceivedStroke);

    }
}