package com.bb.webcanvasservice.security.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 인증 서비스 내부 응답 DTO
 */
@Schema(description = "인증 서비스 내부 응답 DTO")
public record AuthenticationInnerResponse(
        /**
         * 서버에서 생성된 유저의 fingerprint
         */
        @Schema(description = "서버에서 생성된 유저의 fingerprint")
        String fingerprint,

        /**
         * 인증에 사용되는 access token
         */
        @Schema(description = "인증에 사용되는 access token")
        String accessToken,

        /**
         * access token 만료시 refresh에 사용되는 토큰
         */
        @Schema(description = "access token 만료시 refresh에 사용되는 토큰")
        String refreshToken,

        /**
         * refresh token 재발급 여부
         */
        @Schema(description = "refresh token 재발급 여부")
        boolean refreshTokenReissued
) {
}
