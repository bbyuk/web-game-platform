package com.bb.webcanvasservice.infrastructure.security.http.config;

import com.bb.webcanvasservice.common.util.JwtManager;
import com.bb.webcanvasservice.infrastructure.security.http.HttpAuthenticationArgumentResolver;
import com.bb.webcanvasservice.infrastructure.security.http.authentication.HttpAuthenticationEntryPoint;
import com.bb.webcanvasservice.infrastructure.security.http.authorization.HttpAccessDeniedHandler;
import com.bb.webcanvasservice.infrastructure.security.http.filter.JwtAuthenticationFilter;
import com.bb.webcanvasservice.infrastructure.security.http.filter.ApplicationSecurityExceptionHandlingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * Spring Security, Spring Security Messaging 설정 클래스
 * <p>
 * Authentication 객체 공통 처리를 위한 ArgumentResolver 추가를 위해 WebMvcConfigurer implements 하도록 설정 (25.04.03)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class HttpSecurityConfig implements WebMvcConfigurer {

    private final JwtManager jwtManager;
    private final HttpAuthenticationArgumentResolver httpAuthenticationArgumentResolver;
    private final SecurityProperties securityProperties;
    private final HttpAuthenticationEntryPoint authenticationEntryPoint;
    private final HttpAccessDeniedHandler accessDeniedHandler;

    private final List<String> whiteList = List.of("/auth/login");

    /**
     * 웹 http 요청의 인증 처리를 위한 SecurityFilterChain 설정
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(configurer ->
                        configurer.configurationSource(request -> {
                            var config = new org.springframework.web.cors.CorsConfiguration();
                            config.setAllowedOrigins(List.of("http://localhost:5173"));
                            config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                            config.setAllowedHeaders(List.of("*"));
                            config.setAllowCredentials(true); // 쿠키 등 인증정보 포함 허용
                            return config;
                        })
                )
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(securityProperties.whiteList()
                                .stream()
                                .map(AntPathRequestMatcher::new)
                                .toArray(AntPathRequestMatcher[]::new)).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(
                        configurer -> configurer
                                // 인증 실패에 대한 공통 예외 처리
                                .authenticationEntryPoint(authenticationEntryPoint)
                                // 인가 실패에 대한 공통 예외 처리
                                .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtManager, securityProperties), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ApplicationSecurityExceptionHandlingFilter(authenticationEntryPoint), JwtAuthenticationFilter.class)
                .build();
    }


    /**
     * 커스텀 Argument Resolver 등록.
     * Web 핸들러에서 Custom Authentication 객체(WebCanvasAuthentication)를 주입받을 수 있도록 설정.
     *
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(httpAuthenticationArgumentResolver);
    }

}
