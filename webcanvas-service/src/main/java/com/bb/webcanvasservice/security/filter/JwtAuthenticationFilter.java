package com.bb.webcanvasservice.security.filter;

import com.bb.webcanvasservice.security.JwtTokenManager;
import com.bb.webcanvasservice.security.WebCanvasAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenManager jwtTokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenManager.resolveToken(request);

        if (token != null && jwtTokenManager.validateToken(token)) {
            Long userId = jwtTokenManager.getUserIdFromToken(token);

            SecurityContextHolder.getContext().setAuthentication(new WebCanvasAuthentication(userId));
        }

        filterChain.doFilter(request, response);
    }
}
