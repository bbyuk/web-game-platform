package com.bb.webcanvasservice.game.application.command;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "다음 턴으로 진행 커맨드")
public record ProcessToNextTurnCommand(

        @Schema(description = "대상 게임 방 ID")
        Long gameRoomId,

        @Schema(description = "대상 게임 세션 ID")
        Long gameSessionId,

        @Schema(description = "턴 period")
        int period,

        @Schema(description = "정답 여부")
        boolean answered,

        @Schema(description = "다음 턴 시작 딜레이 초")
        int startDelaySeconds
) {}
