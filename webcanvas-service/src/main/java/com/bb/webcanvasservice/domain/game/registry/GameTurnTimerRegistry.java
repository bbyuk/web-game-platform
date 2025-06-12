package com.bb.webcanvasservice.domain.game.registry;

import com.bb.webcanvasservice.domain.game.model.GameTurnTimer;

/**
 * 게임 턴 타이머의 참조를 저장
 */
public interface GameTurnTimerRegistry {
    void register(Long gameRoomId, GameTurnTimer timer);
    GameTurnTimer get(Long gameRoomId);
    void remove(Long gameRoomId);
    boolean contains(Long gameRoomId);
}
