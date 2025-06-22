package com.bb.webcanvasservice.game.application.command;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "presentation layer -> application layer 게임 방 입장 커맨드")
public record JoinGameRoomCommand(

        @Schema(description = "게임 방 ID")
        Long gameRoomId,

        @Schema(description = "입장 요청 유저 ID")
        Long userId
) {
}
