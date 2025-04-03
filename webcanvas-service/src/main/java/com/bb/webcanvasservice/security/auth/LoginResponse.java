package com.bb.webcanvasservice.security.auth;

/**
 * 로그인 응답 record
 * @param accessToken
 * @param refreshToken
 */
public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
