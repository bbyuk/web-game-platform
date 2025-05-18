package com.bb.webcanvasservice.domain.game.event;

/**
 * 게임 방 입장 이벤트 발생시 pub 이벤트
 * @param gameRoomId
 * @param userId
 */
public record GameRoomEntranceEvent(
        Long gameRoomId,
        Long userId
) {
}
