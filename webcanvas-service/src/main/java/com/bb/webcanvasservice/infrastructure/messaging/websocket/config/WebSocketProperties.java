package com.bb.webcanvasservice.infrastructure.messaging.websocket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "websocket")
public record WebSocketProperties(
        String endpoint,
        Topic topic,
        List<String> allowedOriginPatterns,
        int abnormalAccessLimit
) {
    /**
     * 웹소켓 등록 토픽 목록
     * @param main
     * @param sub
     */
    public record Topic(
        MainTopic main,
        SubTopic sub
    ) {
        public record MainTopic(
                String gameRoom,
                String gameSession
        ) {}
        public record SubTopic(
                String canvas,
                String chat
        ) {}
    }
}
