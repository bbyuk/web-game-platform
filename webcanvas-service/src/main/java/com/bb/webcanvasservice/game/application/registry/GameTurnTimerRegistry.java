package com.bb.webcanvasservice.game.application.registry;

import com.bb.webcanvasservice.game.application.service.GameTurnTimer;

/**
 * 게임 턴 타이머의 참조를 저장
 */
public interface GameTurnTimerRegistry {
    void register(Long gameRoomId, GameTurnTimer timer);
    GameTurnTimer get(Long gameRoomId);
    void remove(Long gameRoomId);
    boolean contains(Long gameRoomId);
}
