package com.bb.webcanvasservice.game.application.dto;

import com.bb.webcanvasservice.game.domain.model.GameRoomState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "application layer -> presentation layer 게임 방 상세 정보 DTO")
public record GameRoomEntranceDetailInfoDto(
        @Schema(description = "게임 방 ID", example = "512")
        Long gameRoomId,

        @Schema(description = "게임 방 입장 ID", example = "2231")
        Long gameRoomEntranceId,

        @Schema(description = "현재 게임방에 접속해있는 유저들의 정보")
        List<EnteredUserInfoDto> enteredUsers,

        @Schema(description = "현재 입장한 게임 방의 상태")
        GameRoomState gameRoomState,

        @Schema(description = "요청한 유저의 정보")
        EnteredUserInfoDto requesterUserSummary
) {
}
