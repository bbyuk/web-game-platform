package com.bb.webcanvasservice.infrastructure.security.http.filter;

import com.bb.webcanvasservice.infrastructure.security.http.config.SecurityProperties;
import com.bb.webcanvasservice.common.util.JwtManager;
import com.bb.webcanvasservice.infrastructure.security.http.WebCanvasAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Http 요청에 대해 JWT 토큰 검증을 수행하는 필터
 * SecurityFilterChain에 등록
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtManager jwtManager;
    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtManager.resolveBearerTokenValue(request.getHeader(JwtManager.BEARER_TOKEN));

        jwtManager.validateToken(token);
        Long userId = jwtManager.getUserIdFromToken(token);

        SecurityContextHolder.getContext().setAuthentication(new WebCanvasAuthentication(userId));

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return securityProperties
                .whiteList()
                .stream()
                .map(AntPathRequestMatcher::new)
                .anyMatch(antPathRequestMatcher -> antPathRequestMatcher.matches(request));
    }
}
