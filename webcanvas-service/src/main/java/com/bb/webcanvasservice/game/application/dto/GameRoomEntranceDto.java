package com.bb.webcanvasservice.game.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "application layer to presentation layer GameRoomEntrance DTO")
public record GameRoomEntranceDto(


        @Schema(description = "게임 방 ID")
        Long gameRoomId,

        @Schema(description = "게임 방 입장 ID")
        Long gameRoomEntranceId
) {
}
