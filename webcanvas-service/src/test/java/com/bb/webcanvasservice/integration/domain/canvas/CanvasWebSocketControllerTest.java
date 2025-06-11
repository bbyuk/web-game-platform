package com.bb.webcanvasservice.integration.domain.canvas;

import com.bb.webcanvasservice.TestWebSocketClientFactory;
import com.bb.webcanvasservice.common.util.JwtManager;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.domain.game.service.GameRoomFacade;
import com.bb.webcanvasservice.domain.user.entity.User;
import com.bb.webcanvasservice.infrastructure.persistence.user.UserJpaRepository;
import com.bb.webcanvasservice.websocket.properties.WebSocketProperties;
import com.bb.webcanvasservice.websocket.registry.SessionRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Transactional
@ActiveProfiles("canvas-integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestWebSocketClientFactory.class)
@DisplayName("[unit] [presentation] canvas 웹소켓 컨트롤러 단위 테스트")
class CanvasWebSocketControllerTest {

    /**
     * TODO 토큰 파싱에 시간이 오래걸리는듯 하다. 튜닝포인트
     */

    /**
     * 브로드캐스팅 타임아웃
     * 브로드캐스팅 시간 테스트
     */
    private final int BROADCASTING_TIMEOUT = 5000;

    @Autowired
    TestWebSocketClientFactory testWebSocketClientFactory;


    @LocalServerPort
    private int port;

    @Autowired
    private WebSocketProperties webSocketProperties;

    private String getStrokeSendDestination(Long gameRoomId) {
        return String.format("/session/%d/canvas/stroke", gameRoomId);
    }

    /**
     * WebSocket 연결 client
     */
    private StompSession testUserSession;

    /**
     * 테스트에 필요한 bean 목록
     */
    @Autowired
    private JwtManager jwtManager;
    @Autowired
    private GameRoomFacade gameRoomFacade;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private CanvasTestDataLoader testDataLoader;
    @Autowired
    private SessionRegistry sessionRegistry;


    @BeforeEach
    void setup() throws Exception {
        /**
         * test 유저 생성
         * CanvasTestDataLoader 클래스로 테스트 데이터 이동
         */

        /**
         * WebSocket Session 연결
         */
        testUserSession = testWebSocketClientFactory.connect(testDataLoader.testUser1, port);
    }

    @AfterEach
    void clearSession() {
        sessionRegistry.clear();
    }

    @Test
    @DisplayName("게임 방 입장시 게임방과 관련된 캔버스 웹소켓 이벤트 구독 요청 처리 - 현재 입장한 방의 캔버스만 구독할 수 있다.")
    void testWebSocketSubscriptionValidation() throws Exception {
        // given
        // 게임 방에 접속해있지 않으나, 다른 게임방의 웹소켓 이벤트를 구독하려는 악의적인 유저
        User maliciousUser = userJpaRepository.save(new User(UUID.randomUUID().toString()));
        StompSession maliciousUserSession = testWebSocketClientFactory.connect(maliciousUser, port);
        
        
        // when
        // 아무 방에 접속해있지 않은 상태 -> test GameRoom의 웹소켓에 악의적으로 구독 요청
        CompletableFuture<Stroke> subscribeFuture = testWebSocketClientFactory.subscribe(
                maliciousUserSession,
                testWebSocketClientFactory.getTopic
                        (webSocketProperties.topic().main().gameRoom(),
                                String.valueOf(testDataLoader.testGameRoom.getId()),
                                webSocketProperties.topic().sub().canvas()
                        ),
                Stroke.class
        );

        // then
        Assertions.assertThatThrownBy(() -> subscribeFuture.get(3, TimeUnit.SECONDS))
                .isInstanceOf(TimeoutException.class);
    }

    @Test
    @DisplayName("웹소켓 접속 및 stroke 처리 - canvas 웹 소켓에 연결하고 stroke 이벤트 send시 같은 방에 입장해 웹 소켓에 연결되어 있는 클라이언트로 stroke 메세지를 전송한다.")
    void testWebSocketDrawMessage() throws Exception {
        // given
        // 방에 접속해 있어야한다.
        StompSession subUserSession = testWebSocketClientFactory.connect(testDataLoader.testUser2, port);
        CompletableFuture<Stroke> subscribeFuture = testWebSocketClientFactory.subscribe(
                subUserSession,
                testWebSocketClientFactory.getTopic
                        (webSocketProperties.topic().main().gameRoom(),
                                String.valueOf(testDataLoader.testGameRoom.getId()),
                                webSocketProperties.topic().sub().canvas()
                        ),
                Stroke.class
        );
        Stroke testStroke = testDataLoader.testStroke;

        // when
        testUserSession.send(getStrokeSendDestination(testDataLoader.testGameRoom.getId()), testStroke);

        // then
        Stroke result = subscribeFuture.get(BROADCASTING_TIMEOUT, TimeUnit.MILLISECONDS);

        Assertions.assertThat(result.lineWidth()).isEqualTo(testStroke.lineWidth());
        Assertions.assertThat(result.points().size()).isEqualTo(testStroke.points().size());
    }


    @Test
    @DisplayName("웹 소켓 접속 및 strke 처리 - 같은 방에 입장한 클라이언트는 stroke 이벤트를 받아볼 수 있다.")
    void testWebSocketDrawMessageAtOtherClients() throws Exception {
        // given
        /**
         * 클라이언트에서 처리해야 할 웹소켓 접속 시나리오 로직
         */
        StompSession otherUser1Session = testWebSocketClientFactory.connect(testDataLoader.testUser2, port);
        CompletableFuture<Stroke> otherUser1CompletableFuture = testWebSocketClientFactory.subscribe(
                otherUser1Session,
                testWebSocketClientFactory.getTopic
                        (webSocketProperties.topic().main().gameRoom(),
                                String.valueOf(testDataLoader.testGameRoom.getId()),
                                webSocketProperties.topic().sub().canvas()
                        ),
                Stroke.class
        );

        StompSession otherUser2Session = testWebSocketClientFactory.connect(testDataLoader.testUser3, port);
        CompletableFuture<Stroke> otherUser2CompletableFuture = testWebSocketClientFactory.subscribe(
                otherUser2Session,
                testWebSocketClientFactory.getTopic
                        (webSocketProperties.topic().main().gameRoom(),
                                String.valueOf(testDataLoader.testGameRoom.getId()),
                                webSocketProperties.topic().sub().canvas()
                        ),
                Stroke.class
        );

        /**
         * 현재까지 테스트 유저를 포함해 otherUser1, otherUser2까지 총 3명의 유저가 gameRoomId에 해당하는 게임 방에 입장해있는 상태
         */

        // when
        StompHeaders messageHeader = new StompHeaders();
        messageHeader.setDestination(getStrokeSendDestination(testDataLoader.testGameRoom.getId()));

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