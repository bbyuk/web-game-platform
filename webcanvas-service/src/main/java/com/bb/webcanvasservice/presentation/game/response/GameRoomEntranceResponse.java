package com.bb.webcanvasservice.presentation.game.response;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "게임 방 입장 API 응답 DTO")
public record GameRoomEntranceResponse(

        /**
         * 게임 방 ID
         */
        @Schema(description = "게임 방 ID")
        Long gameRoomId,
        /**
         * 게임 방 입장 ID
         */
        @Schema(description = "게임 방 입장 ID")
        Long gameRoomEntranceId
) {
}
