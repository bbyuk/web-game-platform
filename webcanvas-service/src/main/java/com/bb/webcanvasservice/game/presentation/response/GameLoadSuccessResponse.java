package com.bb.webcanvasservice.game.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 세션 토픽을 구독 성공하고, 로딩 완료 처리하는 API 응답")
public record GameLoadSuccessResponse(
        @Schema(description = "성공 여부")
        boolean success
) {
}
