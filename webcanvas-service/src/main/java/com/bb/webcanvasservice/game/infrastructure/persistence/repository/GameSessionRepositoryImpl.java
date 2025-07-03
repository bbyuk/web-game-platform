package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.game.domain.model.session.GameSession;
import com.bb.webcanvasservice.game.domain.repository.GameSessionRepository;
import com.bb.webcanvasservice.game.infrastructure.persistence.mapper.GameModelMapper;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TODO GameSessionRepository 구현
 */
@Repository
@RequiredArgsConstructor
public class GameSessionRepositoryImpl implements GameSessionRepository {

    private final GameRoomJpaRepository gameRoomRepository;
    private final GameSessionJpaRepository gameSessionJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final GameRoomJpaRepository gameRoomJpaRepository;

    @Override
    public GameSession save(GameSession gameSession) {
        return null;
    }

    @Override
    public Optional<GameSession> findGameSessionById(Long gameSessionId) {
        return Optional.empty();
    }

    @Override
    public Optional<GameSession> findCurrentGameSessionByGameRoomId(Long gameRoomId) {
        return Optional.empty();
    }
}
