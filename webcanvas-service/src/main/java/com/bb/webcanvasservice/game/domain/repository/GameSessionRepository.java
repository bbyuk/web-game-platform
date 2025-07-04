package com.bb.webcanvasservice.game.domain.repository;

import com.bb.webcanvasservice.game.domain.model.session.GameSession;

import java.util.Optional;

/**
 * 게임 세션과 관련된 도메인 레포지토리
 */
public interface GameSessionRepository {
    GameSession save(GameSession gameSession);

    Optional<GameSession> findGameSessionById(Long gameSessionId);

    Optional<GameSession> findCurrentGameSessionByGameRoomId(Long gameRoomId);
}
