package com.bb.webcanvasservice.game.application.command;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * presentation layer -> application layer
 * @param gameRoomParticipantId 게임 방 입장자 ID
 * @param userId 입장 유저 ID
 */
@Schema(description = "게임 방 퇴장 커맨드")
public record ExitGameRoomCommand(
        @Schema(description = "게임 방 입장자 ID")
        Long gameRoomParticipantId,

        @Schema(description = "유저 ID")
        Long userId) {
}
