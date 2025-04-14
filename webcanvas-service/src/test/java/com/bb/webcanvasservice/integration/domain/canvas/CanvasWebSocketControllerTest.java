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
import java.util.concurrent.TimeoutException;

@Transactional
@ActiveProfiles("canvas-integration-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CanvasWebSocketControllerTest {

    /**
     * TODO 토큰 파싱에 시간이 오래걸리는듯 하다. 튜닝포인트
     */
    
    /**
     * 브로드캐스팅 타임아웃
     * 브로드캐스팅 시간 테스트
     */
    private final int BROADCASTING_TIMEOUT = 1500;

    /**
     * 방 입장시 소켓 구독 처리에 대한 타임아웃
     * 3s
     */
    private final int ROOM_ENTRANCE_CANVAS_SUBSCRIBE_TIMEOUT = 5000;

    private final int tokenExpiration = 3600000;


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
        testUserJwt = jwtManager.generateToken(testDataLoader.testUser1.getId(), testDataLoader.testUser1.getFingerprint(), tokenExpiration);
        testUserSession = connect(testUserClient, testUserJwt);
    }

    @Test
    @DisplayName("게임 방 입장시 게임방과 관련된 캔버스 웹소켓 이벤트 구독 요청 처리 - 현재 입장한 방의 캔버스만 구독할 수 있다.")
    void testWebSocketSubscriptionValidation() throws Exception {
        // given
        // 게임 방에 접속해있지 않으나, 다른 게임방의 웹소켓 이벤트를 구독하려는 악의적인 유저
        User maliciousUser = userRepository.save(new User(UUID.randomUUID().toString()));
        
        WebSocketStompClient maliciousUserClient = createClient();
        String maliciousUserJwt = jwtManager.generateToken(maliciousUser.getId(), maliciousUser.getFingerprint(), tokenExpiration);
        StompSession maliciousUserSession = connect(maliciousUserClient, maliciousUserJwt);

        // when
        // 아무 방에 접속해있지 않은 상태 -> test GameRoom의 웹소켓에 악의적으로 구독 요청
        CompletableFuture<Stroke> subscribeFuture = subscribeRoomCanvas(maliciousUserSession, testDataLoader.testGameRoom.getId());

        // then
        Assertions.assertThatThrownBy(() -> subscribeFuture.get(3, TimeUnit.SECONDS))
                .isInstanceOf(TimeoutException.class);
    }

    @Test
    @DisplayName("웹소켓 접속 및 stroke 처리 - canvas 웹 소켓에 연결하고 stroke 이벤트 send시 같은 방에 입장해 웹 소켓에 연결되어 있는 클라이언트로 stroke 메세지를 전송한다.")
    void testWebSocketDrawMessage() throws Exception {
        // given
        // 방에 접속해 있어야한다.
        WebSocketStompClient subUserClient = createClient();

        String subUserJwt = jwtManager.generateToken(testDataLoader.testUser2.getId(), testDataLoader.testUser2.getFingerprint(), tokenExpiration);
        StompSession subUserSession = connect(subUserClient, subUserJwt);

        // 구독
        CompletableFuture<Stroke> subscribeFuture = subscribeRoomCanvas(subUserSession, testDataLoader.testGameRoom.getId());

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
         * 클라이언트에서 처리해야 할 웹소켓 접속 시나리오 로직
         */
        WebSocketStompClient otherUser1Client = createClient();
        String otherUser1Jwt = jwtManager.generateToken(testDataLoader.testUser2.getId(), testDataLoader.testUser2.getFingerprint(), tokenExpiration);
        StompSession otherUser1Session = connect(otherUser1Client, otherUser1Jwt);
        CompletableFuture<Stroke> otherUser1CompletableFuture = subscribeRoomCanvas(otherUser1Session, testDataLoader.testGameRoom.getId());

        WebSocketStompClient otherUser2Client = createClient();
        String otherUser2Jwt = jwtManager.generateToken(testDataLoader.testUser3.getId(), testDataLoader.testUser3.getFingerprint(), tokenExpiration);
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