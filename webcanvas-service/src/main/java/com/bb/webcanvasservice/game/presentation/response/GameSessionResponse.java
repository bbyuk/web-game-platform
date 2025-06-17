package com.bb.webcanvasservice.game.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현재 게임 세션 조회 API의 응답 DTO")
public record GameSessionResponse(

        @Schema(description = "게임 세션 ID")
        Long gameSessionId,

        @Schema(description = "게임 세션 상태")
        String state,

        @Schema(description = "게임 세션 turn 별 타임")
        int timePerTurn,

        @Schema(description = "게임 세션의 현재 진행된 턴 수")
        int currentTurnIndex,

        @Schema(description = "게임 세션의 총 턴 수")
        int turnCount
) {
}
