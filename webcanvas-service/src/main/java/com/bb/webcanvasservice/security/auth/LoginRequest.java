package com.bb.webcanvasservice.security.auth;

/**
 * 로그인 요청 record
 * @param fingerprint fingerprint.js로 취득한 클라이언트 fingerprint
 */
public record LoginRequest(
        String fingerprint
) {
}
