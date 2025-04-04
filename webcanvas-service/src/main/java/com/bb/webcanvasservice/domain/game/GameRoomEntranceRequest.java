package com.bb.webcanvasservice.domain.game;

/**
 * 게임 방 입장 요청 DTO
 * @param joinCode
 */
public record GameRoomEntranceRequest(
        /**
         * 게임 방 입장 코드
         */
        String joinCode
) {
}
