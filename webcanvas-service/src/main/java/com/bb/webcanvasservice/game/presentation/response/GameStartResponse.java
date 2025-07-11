package com.bb.webcanvasservice.game.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 시작 API 응답 DTO")
public record GameStartResponse(
        Long gameSessionId
) {
}
