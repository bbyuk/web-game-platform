package com.bb.webcanvasservice.domain.game;

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
}
