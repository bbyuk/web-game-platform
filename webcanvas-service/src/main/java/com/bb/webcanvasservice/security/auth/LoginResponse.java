package com.bb.webcanvasservice.security.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
