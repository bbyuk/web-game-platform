package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.game.domain.model.session.GameSession;
import com.bb.webcanvasservice.game.domain.model.session.GameSessionState;
import com.bb.webcanvasservice.game.domain.repository.GameSessionRepository;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GamePlayerJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameSessionJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameTurnJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.mapper.GameModelMapper;
import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * GameSessionRepository 구현
 */
@Repository
@RequiredArgsConstructor
public class GameSessionRepositoryImpl implements GameSessionRepository {

    /**
     * aggregate internal jpa repository
     */
    private final GameSessionJpaRepository gameSessionJpaRepository;
    private final GamePlayerJpaRepository gamePlayerJpaRepository;
    private final GameTurnJpaRepository gameTurnJpaRepository;

    /**
     * aggregate external jpa repository
     */
    private final UserJpaRepository userJpaRepository;
    private final GameRoomJpaRepository gameRoomJpaRepository;

    /**
     * GameSession (root)
     * - List<GameTurn>
     * - List<GamePlayer>
     * 일괄 저장
     *
     * @param gameSession
     * @return 저장 후 재매핑한 GameSession
     */
    @Override
    public GameSession save(GameSession gameSession) {
        GameSessionJpaEntity gameSessionJpaEntity = GameModelMapper.toEntity(
                gameSession,
                gameRoomJpaRepository.findById(gameSession.gameRoomId())
                        .orElseThrow(GameRoomNotFoundException::new)
        );

        GameSessionJpaEntity savedGameSessionJpaEntity = gameSessionJpaRepository.save(gameSessionJpaEntity);

        List<GameTurnJpaEntity> gameTurnJpaEntities = gameSession
                .gameTurns()
                .stream()
                .map(gameTurn -> GameModelMapper.toEntity(gameTurn, gameSessionJpaEntity))
                .collect(Collectors.toList());
        gameTurnJpaRepository.saveAll(gameTurnJpaEntities);

        /**
         * TODO 튜닝 포인트 userJpaRepository에 userId 쿼리 한방쿼리로 변경
         */
        List<GamePlayerJpaEntity> gamePlayerJpaEntities = gameSession
                .gamePlayers()
                .stream()
                .map(gamePlayer -> GameModelMapper.toEntity(
                                gamePlayer,
                                savedGameSessionJpaEntity,
                                userJpaRepository.findById(gamePlayer.userId())
                                        .orElseThrow(UserNotFoundException::new)
                        )
                ).collect(Collectors.toList());
        gamePlayerJpaRepository.saveAll(gamePlayerJpaEntities);

        return GameModelMapper.toModel(
                savedGameSessionJpaEntity,
                gamePlayerJpaEntities,
                gameTurnJpaEntities
        );
    }

    @Override
    public Optional<GameSession> findGameSessionById(Long gameSessionId) {
        GameSessionJpaEntity gameSessionJpaEntity = gameSessionJpaRepository.findById(gameSessionId).orElseThrow(GameSessionNotFoundException::new);
        List<GameTurnJpaEntity> gameTurnJpaEntities = gameTurnJpaRepository.findByGameSessionId(gameSessionId);
        List<GamePlayerJpaEntity> gamePlayerJpaEntities = gamePlayerJpaRepository.findByGameSessionId(gameSessionId);

        return Optional.of(GameModelMapper
                .toModel(
                        gameSessionJpaEntity,
                        gamePlayerJpaEntities,
                        gameTurnJpaEntities
                )
        );
    }

    /**
     * 게엠 방 ID로 현재 게임 세션 찾기
     * @param gameRoomId 게임 방 ID
     * @return 게임 세션
     */
    @Override
    public Optional<GameSession> findCurrentGameSessionByGameRoomId(Long gameRoomId) {
        GameSessionJpaEntity gameSessionJpaEntity = gameSessionJpaRepository.findByGameRoomIdAndStates(gameRoomId, GameSessionState.active).orElseThrow(GameSessionNotFoundException::new);
        List<GameTurnJpaEntity> gameTurnJpaEntities = gameTurnJpaRepository.findByGameSessionId(gameSessionJpaEntity.getId());
        List<GamePlayerJpaEntity> gamePlayerJpaEntities = gamePlayerJpaRepository.findByGameSessionId(gameSessionJpaEntity.getId());

        return Optional.of(GameModelMapper
                .toModel(
                        gameSessionJpaEntity,
                        gamePlayerJpaEntities,
                        gameTurnJpaEntities
                )
        );
    }
}
