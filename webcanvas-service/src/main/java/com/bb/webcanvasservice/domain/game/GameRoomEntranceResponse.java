package com.bb.webcanvasservice.domain.game;

import java.util.List;

/**
 * 게임 방 입장 API 응답 DTO
 * @param gameRoomId
 * @param gameRoomEntranceId
 */
public record GameRoomEntranceResponse(
        /**
         * 게임 방 ID
         */
        Long gameRoomId,
        /**
         * 게임 방 입장 ID
         */
        Long gameRoomEntranceId,
        /**
         * 현재 게임방에 접속해있는 다른 유저들의 정보
         */
        List<EnteredUserSummary> otherUsers

) {
    /**
     * 게임 방에 입장한 유저 정보 DTO
     * @param userId
     */
    public record EnteredUserSummary (
            Long userId
    ) {};
}
