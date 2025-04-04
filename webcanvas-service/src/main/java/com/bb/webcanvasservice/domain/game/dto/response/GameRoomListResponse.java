package com.bb.webcanvasservice.domain.game.dto.response;

import java.util.List;

/**
 * 게임 방 조회 API의 응답 DTO
 * @param roomList
 */
public record GameRoomListResponse(
        /**
         * 게임 방 요약 정보 목록
         */
        List<GameRoomSummary> roomList
) {

    /**
     * 게임 방 조회 API의 응답 내부 Dto
     * 클라이언트에서 필요한 게임 방의 정보를 담는다.
     * @param gameRoomId
     * @param joinCode
     */
    public record GameRoomSummary(
            Long gameRoomId,
            String joinCode
    ) {
    }
}
