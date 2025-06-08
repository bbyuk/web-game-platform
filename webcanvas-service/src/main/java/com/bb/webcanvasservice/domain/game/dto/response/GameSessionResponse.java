package com.bb.webcanvasservice.domain.game.dto.response;

import com.bb.webcanvasservice.domain.game.enums.GameSessionState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현재 게임 세션 조회 API의 응답 DTO")
public record GameSessionResponse(

        @Schema(description = "게임 세션 ID")
        Long gameSessionId,

        @Schema(description = "게임 세션 상태")
        GameSessionState state
) {
}
