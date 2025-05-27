package com.bb.webcanvasservice.domain.game.registry;

import com.bb.webcanvasservice.domain.game.dto.inner.GameTurnTimerEntry;

import java.util.concurrent.ScheduledFuture;

/**
 * 게임 턴 타이머의 참조를 저장
 */
public interface GameTurnTimerRegistry {
    void register(Long gameRoomId, GameTurnTimerEntry timerEntry);
    GameTurnTimerEntry get(Long gameRoomId);
    void remove(Long gameRoomId);
    boolean contains(Long gameRoomId);
}
