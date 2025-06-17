package com.bb.webcanvasservice.auth.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 API 응답 DTO")
public record AuthenticationApiResponse(

        @Schema(description = "유저 식별 시퀀스")
        Long userId,

        @Schema(description = "서버에서 생성된 유저의 fingerprint")
        String fingerprint,

        @Schema(description = "access token")
        String accessToken,

        @Schema(description = "인증 여부")
        boolean success
) {
}
