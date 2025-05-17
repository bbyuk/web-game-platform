package com.bb.webcanvasservice.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 요청 DTO
 */
@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "fingerprint.js로 취득한 클라이언트 fingerprint")
        String fingerprint
) {
}
