package com.bb.webcanvasservice.presentation.game.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 방에 입장한 유저의 레디 상태 변경 요청 DTO")
public record GameRoomReadyUpdateRequest(

        @Schema(description = "레디 상태 여부", example = "true")
        boolean ready
) {
}
