package com.bb.webcanvasservice.security.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 인증 API 응답 DTO
 */
@Schema(description = "인증 API 응답 DTO")
public record AuthenticationApiResponse(

        /**
         * 서버에서 생성된 유저의 fingerprint
         */
        @Schema(description = "서버에서 생성된 유저의 fingerprint")
        String fingerprint,

        /**
         * 인증에 사용되는 access token
         */
        @Schema(description = "인증에 사용되는 access token")
        String accessToken
) {
}
