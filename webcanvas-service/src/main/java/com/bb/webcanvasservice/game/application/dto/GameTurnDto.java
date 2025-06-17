package com.bb.webcanvasservice.game.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "application layer -> presentation layer 게임 턴 DTO")
public record GameTurnDto(
        @Schema(description = "해당 턴의 그림을 그릴 유저의 ID")
        Long drawerId,

        @Schema(description = "해당 턴의 정답. Drawer에게만 공개된다.", nullable = true)
        String answer,

        @Schema(description = "해당 턴의 만료 시간")
        LocalDateTime expiration
) {
}
