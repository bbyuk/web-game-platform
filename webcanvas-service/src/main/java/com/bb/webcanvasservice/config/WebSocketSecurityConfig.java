package com.bb.webcanvasservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * TODO 업데이트 필요
 * 웹소켓 Spring Security 적용시 아직 CSRF 핸들링 기능이 포함되어 있지않아
 * 레거시 방식으로 웹소켓 Security 설정 진행
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    /**
     * 인바운드 규칙 설정
     * 개발중엔 permitAll 처리
     * @param messages
     */
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.anyMessage().permitAll();
    }

    /**
     * CSRF 보호 비활성화
     * @return
     */
    @Override
    protected boolean sameOriginDisabled() {
        return true; 
    }
}