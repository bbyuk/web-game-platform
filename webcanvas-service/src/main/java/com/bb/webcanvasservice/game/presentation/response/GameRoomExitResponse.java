package com.bb.webcanvasservice.game.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 방 퇴장 API 응답 DTO")
public record GameRoomExitResponse(

        @Schema(description = "성공", example = "true")
        boolean success
) {
}
