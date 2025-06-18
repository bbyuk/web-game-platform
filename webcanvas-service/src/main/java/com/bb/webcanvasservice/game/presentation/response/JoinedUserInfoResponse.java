package com.bb.webcanvasservice.game.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게임 방에 입장한 유저 정보 응답 DTO")
public record JoinedUserInfoResponse(

        @Schema(description = "유저 ID", example = "512")
        Long userId,

        @Schema(description = "방 노출 컬러", example = "#1e9000")
        String color,

        @Schema(description = "자동 생성된 게임 방 내 유저 닉네임", example = "고매한 여우")
        String nickname,

        @Schema(description = "게임 방 내에서의 ROLE")
        String role,

        @Schema(description = "준비 여부")
        boolean ready

) {
};