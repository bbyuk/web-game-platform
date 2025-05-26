package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GamePlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 게임 플레이 이력 레포지토리
 */
public interface GamePlayHistoryRepository extends JpaRepository<GamePlayHistory, Long> {
}
