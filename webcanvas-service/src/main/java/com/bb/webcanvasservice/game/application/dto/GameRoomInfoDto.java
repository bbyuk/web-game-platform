package com.bb.webcanvasservice.game.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "application layer -> presentation layer 게임 방 정보 DTO")
public record GameRoomInfoDto(

        @Schema(description = "게임 방 ID")
        Long gameRoomId,

        @Schema(description = "게임 방의 입장정원")
        int capacity,

        @Schema(description = "현재 입장한 인원 수")
        int enterCount,

        @Schema(description = "게임 방 입장 코드")
        String joinCode
) {

}
