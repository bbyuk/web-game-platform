package com.bb.webcanvasservice.security.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 응답 DTO
 */
@Schema(description = "로그인 응답 Dto")
public record LoginResponse(
        /**
         * 인증에 사용되는 access token
         */
        @Schema(description = "인증에 사용되는 access token")
        String accessToken,
        /**
         * access token 만료시 refresh에 사용되는 토큰
         */
        @Schema(description = "access token 만료시 refresh에 사용되는 토큰")
        String refreshToken
) {
}
