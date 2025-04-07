package com.bb.webcanvasservice.domain.game.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 게임 방 입장 요청 DTO
 */
@Schema(description = "게임 방 입장 요청 DTO")
public record GameRoomEntranceRequest(
        /**
         * 게임 방 입장 코드
         */
        @Schema(description = "게임 방 입장 코드", example = "DSK78R")
        String joinCode
) {
}
