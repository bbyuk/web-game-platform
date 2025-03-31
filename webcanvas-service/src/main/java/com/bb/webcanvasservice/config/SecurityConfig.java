package com.bb.webcanvasservice.config;

import com.bb.webcanvasservice.security.JwtChannelInterceptor;
import com.bb.webcanvasservice.security.JwtManager;
import com.bb.webcanvasservice.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Spring Security, Spring Security Messaging 설정 클래스
 */
@Configuration
@EnableWebSecurity
@EnableWebSocketSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtManager jwtManager;

    /**
     * 웹 http 요청의 인증 처리를 위한 SecurityFilterChain 설정
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // TODO 인증 프로세스 개발이 완료되기 전까지 cors disable
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // TODO 인증 프로세스 개발이 완료되기 전까지 모든 http 요청 permit
                .authorizeHttpRequests(configurer -> configurer
                        .anyRequest().permitAll())
                .addFilterBefore(new JwtAuthenticationFilter(jwtManager), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 웹 소켓 메세징 인증 처리를 위한 WebSocket Security 설정
     * @param messages
     * @return
     */
    @Bean
    public AuthorizationManager<Message<?>> authorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        // TODO 인증 프로세스 개발이 완료되기 전까지 모든 웹소켓 메세지 permit
        return messages
                .anyMessage().permitAll()
                .build();
    }

    /**
     * 웹소켓 csrf disable
     * @return
     */
    @Bean
    public ChannelInterceptor csrfChannelInterceptor() {
        return new ChannelInterceptor() {};
    }

    @Bean
    public JwtChannelInterceptor jwtChannelInterceptor() {
        return new JwtChannelInterceptor(jwtManager);
    }

}
