package com.bb.webcanvasservice.application.game.command;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "presentation layer -> application layer 게임 시작 커맨드")
public record StartGameCommand(
        @Schema(description = "게임 시작 요청 대상 게임 방의 ID", example = "23")
        Long gameRoomId,

        @Schema(description = "게임을 구성하는 턴의 수", example = "6")
        int turnCount,

        @Schema(description = "한 턴당 제공되는 시간 (s)", example = "90")
        int timePerTurn,

        @Schema(description = "시작 요청 유저 ID", example = "23")
        Long userId
) {
}
