package com.bb.webcanvasservice.domain.game.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 게임 방 입장 API 응답 DTO
 */
@Schema(description = "게임 방 입장 API 응답 DTO")
public record GameRoomEntranceResponse(
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
        List<EnteredUserSummary> otherUsers

) {
    /**
     * 게임 방에 입장한 유저 정보 DTO
     * @param userId
     */
    @Schema(description = "게임 방에 입장한 유저 정보 DTO")
    public record EnteredUserSummary (
            /**
             * 유저 ID
             */
            @Schema(description = "유저 ID", example = "512")
            Long userId
    ) {};
}
