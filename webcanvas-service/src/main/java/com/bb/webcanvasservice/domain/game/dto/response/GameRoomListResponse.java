package com.bb.webcanvasservice.domain.game.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 게임 방 조회 API의 응답 DTO
 */
@Schema(description = "게임 방 조회 API의 응답 DTO")
public record GameRoomListResponse(
        /**
         * 게임 방 요약 정보 목록
         */
        @Schema(description = "게임 방 요약 정보 목록")
        List<GameRoomSummary> roomList
) {

    /**
     * 게임 방 조회 API 응답 내부 DTO
     * 클라이언트에서 필요한 게임 방의 정보를 담는다.
     */
    @Schema(description = "게임 방 조회 API 응답 내부 DTO - 클라이언트에서 필요한 게임 방의 정보를 담는다.")
    public record GameRoomSummary(

            /**
             * 게임 방 ID
             */
            @Schema(description = "게임 방 ID")
            Long gameRoomId,

            /**
             * 게임 방의 입장정원
             */
            @Schema(description = "게임 방의 입장정원")
            int capacity,

            /**
             * 현재 입장한 인원 수
             */
            @Schema(description = "현재 입장한 인원 수")
            int enterCount,

            /**
             * 게임 방 입장 코드
             */
            @Schema(description = "게임 방 입장 코드")
            String joinCode
    ) {
    }
}
