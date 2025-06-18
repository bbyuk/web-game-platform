package com.bb.webcanvasservice.game.application.command;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * presentation layer -> application layer
 * @param gameRoomParticipantId 게임 방 입장자 ID
 * @param userId 유저 ID
 * @param ready 레디
 */
@Schema(description = "게임 방 입장자 레디 상태 변경 Command")
public record UpdateReadyCommand(
        
        @Schema(description = "게임 방 입장자 ID")
        Long gameRoomParticipantId, 
        
        @Schema(description = "입장자의 유저 ID")
        Long userId, 
        
        @Schema(description = "목표 레디 여부")
        boolean ready
) {
}
