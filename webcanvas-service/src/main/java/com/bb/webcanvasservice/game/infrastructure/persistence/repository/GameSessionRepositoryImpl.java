//package com.bb.webcanvasservice.game.infrastructure.persistence.repository;
//
//import com.bb.webcanvasservice.game.domain.exception.GameRoomNotFoundException;
//import com.bb.webcanvasservice.game.domain.exception.GameSessionNotFoundException;
//import com.bb.webcanvasservice.game.domain.model.gameroom.GameSession;
//import com.bb.webcanvasservice.game.domain.model.gameroom.GameTurn;
//import com.bb.webcanvasservice.game.domain.model.gameroom.GameTurnState;
//import com.bb.webcanvasservice.game.application.repository.GameSessionRepository;
//import com.bb.webcanvasservice.game.infrastructure.persistence.mapper.GameModelMapper;
//import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameTurnJpaEntity;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Repository
//@RequiredArgsConstructor
//public class GameSessionRepositoryImpl implements GameSessionRepository {
//
//    private final GameSessionJpaRepository gameSessionJpaRepository;
//    private final GameRoomJpaRepository gameRoomJpaRepository;
//    private final GameTurnJpaRepository gameTurnJpaRepository;
//
//    private final EntityManager em;
//
//    @Override
//    public Optional<GameSession> findById(Long gameSessionId) {
//        return gameSessionJpaRepository.findById(gameSessionId).map(GameModelMapper::toModel);
//    }
//
//    @Override
//    public GameSession save(GameSession gameSession) {
//        return GameModelMapper.toModel(
//                gameSessionJpaRepository.save(
//                        GameModelMapper.toEntity(
//                                gameSession,
//                                gameRoomJpaRepository.findById(gameSession.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new)
//                        )
//                )
//        );
//    }
//
//    @Override
//    public int findCurrentRound(Long gameSessionId) {
//        return gameSessionJpaRepository.findCurrentRound(gameSessionId);
//    }
//
//    @Override
//    public List<GameSession> findGameSessionsByGameRoomId(Long gameRoomId) {
//        return gameSessionJpaRepository.findGameSessionsByGameRoomId(gameRoomId)
//                .stream()
//                .map(GameModelMapper::toModel)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Optional<GameTurn> findLatestTurn(Long gameSessionId) {
//        try {
//            String jpql = """
//                    select      gt
//                    from        GameTurnJpaEntity gt
//                    where       gt.gameSessionEntity.id =: gameSessionId
//                    order by    gt.id desc
//                    """;
//
//            return em.createQuery(jpql, GameTurnJpaEntity.class)
//                    .setParameter("gameSessionId", gameSessionId)
//                    .setMaxResults(1)
//                    .getResultList()
//                    .stream()
//                    .findFirst()
//                    .map(GameModelMapper::toModel);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return Optional.empty();
//        }
//    }
//
//    @Override
//    public List<GameTurn> findTurnsByGameSessionId(Long gameSessionId) {
//        return gameTurnJpaRepository.findTurnsByGameSessionId(gameSessionId)
//                .stream()
//                .map(GameModelMapper::toModel)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public int findTurnCountByGameSessionId(Long gameSessionId) {
//        return (int) gameTurnJpaRepository.findTurnCountByGameSessionId(gameSessionId);
//    }
//
//    @Override
//    public int findTurnCountByGameSessionIdAndStates(Long gameSessionId, List<GameTurnState> states) {
//        return (int) gameTurnJpaRepository.findTurnCountByGameSessionIdAndStates(gameSessionId, states);
//    }
//
//    @Override
//    public GameTurn saveGameTurn(GameTurn gameTurn) {
//        return GameModelMapper.toModel(
//                gameTurnJpaRepository.save(
//                        GameModelMapper.toEntity(
//                                gameTurn,
//                                gameSessionJpaRepository.findById(gameTurn.getGameSessionId()).orElseThrow(GameSessionNotFoundException::new)
//                        )
//                )
//        );
//    }
//}
