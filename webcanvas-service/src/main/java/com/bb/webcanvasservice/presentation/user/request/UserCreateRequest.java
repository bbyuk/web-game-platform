package com.bb.webcanvasservice.presentation.user.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 유저 생성 요청
 * @param clientFingerprint
 */
@Schema(description = "유저 생성 요청 DTO")
public record UserCreateRequest(
        @Schema(description = "클라이언트로부터 얻은 Fingerprint", example = "3f8d47a3a92b77e5")
        String clientFingerprint
) {
}
