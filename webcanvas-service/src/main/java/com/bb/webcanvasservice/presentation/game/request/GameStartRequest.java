package com.bb.webcanvasservice.presentation.game.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 시작 요청시 클라이언트로부터 받을 파라미터")
public record GameStartRequest(

        @Schema(description = "게임 시작 요청 대상 게임 방의 ID", example = "23")
        Long gameRoomId,

        @Schema(description = "게임을 구성하는 턴의 수", example = "6")
        int turnCount,

        @Schema(description = "한 턴당 제공되는 시간 (s)", example = "90")
        int timePerTurn
) {
}
