package com.bb.webcanvasservice.game.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 방 입장 요청 DTO")
public record GameRoomEntranceRequest(
        @Schema(description = "게임 방 입장 코드", example = "DSK78R")
        String joinCode
) {
}
