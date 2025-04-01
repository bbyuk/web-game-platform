package com.bb.webcanvasservice.domain.user.dto.request;

/**
 * 유저 생성 요청 Payload
 * @param clientFingerprint
 */
public record UserCreateRequest(
        String clientFingerprint
) {
}
