package com.bb.webcanvasservice.security.web;

import com.bb.webcanvasservice.security.auth.JwtManager;
import com.bb.webcanvasservice.security.auth.WebCanvasAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Http 요청에 대해 JWT 토큰 검증을 수행하는 필터
 * SecurityFilterChain에 등록
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtManager jwtManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtManager.resolveToken(request);

        if (token != null && jwtManager.validateToken(token)) {
            Long userId = jwtManager.getUserIdFromToken(token);

            SecurityContextHolder.getContext().setAuthentication(new WebCanvasAuthentication(userId));
        }

        filterChain.doFilter(request, response);
    }
}
