package com.bb.webcanvasservice.game.application.command;

import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "presentation layer -> application layer 게임 방 입장 커맨드")
public record EnterGameRoomCommand(

        @Schema(description = "게임 방 ID")
        Long gameRoomId,

        @Schema(description = "입장 요청 유저 ID")
        Long userId,

        @Schema(description = "게임 방 입장 역할 코드")
        GameRoomParticipantRole role
) {
}
