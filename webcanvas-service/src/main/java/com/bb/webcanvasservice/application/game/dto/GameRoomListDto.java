package com.bb.webcanvasservice.application.game.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "application layer -> presentation layer 게임 방 목록 DTO")
public record GameRoomListDto(
        @Schema(description = "게임 방 정보 목록")
        List<GameRoomInfoDto> roomList
) {
}
