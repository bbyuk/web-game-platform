package com.bb.webcanvasservice.domain.game.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 게임 방 입장 API 응답 DTO
 */
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
