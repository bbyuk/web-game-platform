package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 게임 세션 레포지토리
 */
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
}
