package com.bb.webcanvasservice.security.web;

import com.bb.webcanvasservice.security.auth.JwtManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * Spring Security, Spring Security Messaging 설정 클래스
 *
 * Authentication 객체 공통 처리를 위한 ArgumentResolver 추가를 위해 WebMvcConfigurer implements 하도록 설정 (25.04.03)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {

    private final JwtManager jwtManager;
    private final WebAuthenticationArgumentResolver webAuthenticationArgumentResolver;

    private final List<String> whiteList = List.of("/user");

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
                // TODO 인증 프로세스 개발이 완료되기 전까지 모든 요청 permitAll
                .authorizeHttpRequests(configurer -> configurer
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtManager), UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    /**
     * 커스텀 Argument Resolver 등록.
     * Web 핸들러에서 Custom Authentication 객체(WebCanvasAuthentication)를 주입받을 수 있도록 설정.
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(webAuthenticationArgumentResolver);
    }
}
