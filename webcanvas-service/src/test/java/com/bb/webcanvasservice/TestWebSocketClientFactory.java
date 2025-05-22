package com.bb.webcanvasservice;

import com.bb.webcanvasservice.common.JwtManager;
import com.bb.webcanvasservice.config.properties.WebSocketProperties;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
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
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@TestComponent
public class TestWebSocketClientFactory {
    private final int tokenExpiration = 3600000;

    @Autowired
    private WebSocketProperties webSocketProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtManager jwtManager;

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
     * 연결 후 세션 리턴
     * @param user
     * @return
     * @throws Exception
     */
    public StompSession connect(User user, int port) throws Exception {
        WebSocketStompClient client = createClient();
        String jwt = jwtManager.generateToken(user.getId(), user.getFingerprint(), tokenExpiration);

        // 소켓 connect 헤더
        // 웹소켓 연결 이전에 platform-service 로부터 토큰 발급받음.
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(JwtManager.BEARER_TOKEN, String.format("%s%s", JwtManager.BEARER_TOKEN_PREFIX, jwt));
        return client.connectAsync(String.format("ws://localhost:%d%s", port, webSocketProperties.endpoint()), new WebSocketHttpHeaders(), stompHeaders, new StompSessionHandlerAdapter() {}).get();
    }

    public <T> CompletableFuture<T> subscribe(StompSession session, String topic, Class<T> clazz) {
        CompletableFuture<T> asyncResult = new CompletableFuture<>();

        session.subscribe(topic, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return clazz;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    System.out.println("payload = " + payload);
                    asyncResult.complete(clazz.cast(payload));
                }
                catch(ClassCastException e) {
                    asyncResult.completeExceptionally(e);
                }
            }
        });

        return asyncResult;
    }

    public String getTopic(String... parts) {
        return Arrays.stream(parts).collect(Collectors.joining("/"));
    }
}
