package com.bb.webcanvasservice.presentation.auth.util;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.web.security.SecurityProperties;
import com.bb.webcanvasservice.web.security.exception.ApplicationAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AuthenticationCookieManager {

    private final SecurityProperties securityProperties;

    public ResponseCookie getRefreshTokenResponseCookie(String value) {
        return ResponseCookie.from(securityProperties.cookie().refreshToken(), value)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(securityProperties.refreshTokenExpirationSeconds())
                .sameSite("Lax")
                .build();
    }

    public String parseRefreshCookieFrom(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(securityProperties.cookie().refreshToken()))
                .findFirst()
                .orElseThrow(() -> new ApplicationAuthenticationException(ErrorCode.REFRESH_TOKEN_NOT_FOUND))
                .getValue();
    }
}
