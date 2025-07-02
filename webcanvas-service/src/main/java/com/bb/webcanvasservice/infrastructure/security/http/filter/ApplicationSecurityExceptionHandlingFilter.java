package com.bb.webcanvasservice.infrastructure.security.http.filter;

import com.bb.webcanvasservice.infrastructure.security.http.exception.ApplicationAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * SecurityFilterChain내에서 발생하는 ApplicationAuthenticationException에 대해서 공통처리를 수행하는 Filter
 */
@RequiredArgsConstructor
public class ApplicationSecurityExceptionHandlingFilter extends OncePerRequestFilter {

    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        }
        catch (ApplicationAuthenticationException e) {
            /**
             * ApplicationAuthenticationException에 대해서만 handling
             * 그 외의 Spring Security에서 관리되는 AuthenticationException은 ExceptionTranslationFilter에서 처리되도록 둔다.
             */
            authenticationEntryPoint.commence(request, response, e);
        }
    }
}
