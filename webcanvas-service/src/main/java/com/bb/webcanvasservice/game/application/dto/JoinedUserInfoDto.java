package com.bb.webcanvasservice.game.application.dto;

import com.bb.webcanvasservice.game.domain.model.participant.GameRoomParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * application layer -> presentation layer 입장된 유저 정보 DTO
 */
public record JoinedUserInfoDto(
        @Schema(description = "유저 ID", example = "512")
        Long userId,

        @Schema(description = "방 노출 컬러", example = "#1e9000")
        String color,

        @Schema(description = "자동 생성된 게임 방 내 유저 닉네임", example = "고매한 여우")
        String nickname,

        @Schema(description = "게임 방 내에서의 ROLE")
        GameRoomParticipantRole role,

        @Schema(description = "레디 여부")
        boolean ready
) {
}
