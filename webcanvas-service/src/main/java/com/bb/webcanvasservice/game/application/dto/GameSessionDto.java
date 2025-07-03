package com.bb.webcanvasservice.game.application.dto;

import com.bb.webcanvasservice.game.domain.model.session.GameSessionState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "application layer -> presentation layer GameSession 정보 DTO")
public record GameSessionDto(
        @Schema(description = "게임 세션 ID")
        Long gameSessionId,

        @Schema(description = "게임 세션 상태")
        GameSessionState state,

        @Schema(description = "게임 세션 turn 별 타임")
        int timePerTurn,

        @Schema(description = "게임 세션의 현재 진행된 턴 수")
        int currentTurnIndex,

        @Schema(description = "게임 세션의 총 턴 수")
        int turnCount
) {
}
