package com.bb.webcanvasservice.domain.game.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 입장한 게임 방 정보 조회 API 응답 DTO
 */
@Schema(description = "입장한 게임 방 정보 조회 API 응답 DTO")
public record GameRoomEntranceInfoResponse(
        /**
         * 게임 방 ID
         */
        @Schema(description = "게임 방 ID", example = "512")
        Long gameRoomId,

        /**
         * 게임 방 입장 ID
         */
        @Schema(description = "게임 방 입장 ID", example = "2231")
        Long gameRoomEntranceId,

        /**
         * 현재 게임방에 접속해있는 다른 유저들의 정보
         */
        @Schema(description = "현재 게임방에 접속해있는 다른 유저들의 정보")
        List<EnteredUserSummary> enteredUsers

) {
    /**
     * 게임 방에 입장한 유저 정보 DTO
     * @param userId
     */
    @Schema(description = "게임 방에 입장한 유저 정보 DTO")
    public record EnteredUserSummary (

            @Schema(description = "유저 ID", example = "512")
            Long userId,

            @Schema(description = "방 노출 컬러", example = "#1e9000")
            String color,

            @Schema(description = "자동 생성된 게임 방 내 유저 닉네임", example = "순수한 플레이어")
            String label
    ) {};
}
