package com.bb.webcanvasservice.domain.game.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 방 생성 응답 DTO")
public record GameRoomCreateResponse(
        /**
         * 게임 방 ID
         */
        @Schema(description = "게임 방 ID", example = "22")
        Long gameRoomId,

        /**
         * 게임 방 입장 ID
         */
        @Schema(description = "게임 방 입장 ID", example = "153")
        Long gameRoomEntranceId
) {
}
