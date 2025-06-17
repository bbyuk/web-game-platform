package com.bb.webcanvasservice.auth.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 요청 DTO
 */
@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "서버에서 유저 생성시 함께 생성된 클라이언트 fingerprint")
        String fingerprint
) {
}
