package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 게임 턴을 저장하는 Repository
 */
public interface GameTurnRepository extends JpaRepository<GameTurn, Long> {
}
