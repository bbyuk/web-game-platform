package com.bb.webcanvasservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "websocket")
public record WebSocketProperties(
        String endpoint,
        List<String> allowedOriginPatterns,
        List<String> enabledBrokers
) {}
