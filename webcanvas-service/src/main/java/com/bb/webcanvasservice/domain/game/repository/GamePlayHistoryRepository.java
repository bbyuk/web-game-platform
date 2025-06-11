package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.model.GamePlayHistory;

import java.util.List;

/**
 * 게임 플레이 이력 레포지토리
 */
public interface GamePlayHistoryRepository {
    List<GamePlayHistory> findByGameSessionId(Long gameSessionId);
}
