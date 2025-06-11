package com.bb.webcanvasservice.presentation.game.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 방 조회 API 응답 내부 DTO - 클라이언트에서 필요한 게임 방의 정보를 담는다.")
public record GameRoomInfoResponse(
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