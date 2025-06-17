package com.bb.webcanvasservice.auth.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 서비스 내부 응답 DTO")
public record AuthenticationInnerResponse(

        @Schema(description = "user 식별 시퀀스")
        Long userId,

        @Schema(description = "서버에서 생성된 유저의 fingerprint")
        String fingerprint,

        @Schema(description = "인증에 사용되는 access token")
        String accessToken,

        @Schema(description = "access token 만료시 refresh에 사용되는 토큰")
        String refreshToken,

        @Schema(description = "refresh token 재발급 여부")
        boolean refreshTokenReissued
) {
}
