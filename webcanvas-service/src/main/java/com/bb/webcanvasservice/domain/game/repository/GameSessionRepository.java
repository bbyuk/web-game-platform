package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.model.GameSession;
import com.bb.webcanvasservice.domain.game.model.GameTurn;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameTurnJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 게임 플레이 및 세션, 턴 관련 도메인 레포지토리
 */
public interface GameSessionRepository {

    /**
     * 게임 세션을 ID로 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 게임 세션 객체
     */
    Optional<GameSession> findById(Long gameSessionId);

    /**
     * 게임 세션을 저장한다.
     * @param newGameSession 대상 게임 세션 객체
     * @return 저장된 GameSession 객체
     */
    GameSession save(GameSession newGameSession);

    /**
     * 현재 라운드를 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 현재 라운드
     */
    int findCurrentRound(Long gameSessionId);

    /**
     * 조회 대상 게임 방에서 진행된 GameSession 목록을 조회한다.
     * @param gameRoomId 게임 방 ID
     * @return 게임 세션 목록
     */
    List<GameSession> findGameSessionsByGameRoomId(Long gameRoomId);

    /**
     * 가장 최신의 턴을 조회한다.
     * @param gameSessionId
     * @return
     */
    Optional<GameTurn> findLatestTurn(Long gameSessionId);

    /**
     * 게임 세션 ID로 해당 세션에 포함된 게임 턴 목록을 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 턴 목록
     */
    List<GameTurn> findTurnsByGameSessionId(Long gameSessionId);

    /**
     * 게임 세션 ID로 해당 세션에 포함된 게임 턴 수를 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 턴 수
     */
    long findTurnCountByGameSessionId(Long gameSessionId);

    /**
     * 게임 턴을 저장한다.
     * @param gameTurn 게임 턴
     * @return 저장된 게임 턴
     */
    GameTurn saveGameTurn(GameTurn gameTurn);
}
