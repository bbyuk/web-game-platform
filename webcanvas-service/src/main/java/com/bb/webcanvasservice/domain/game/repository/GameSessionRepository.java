package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 게임 세션 레포지토리
 */
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    /**
     * 현재 라운드를 조회한다.
     * @param gameSessionId
     * @return
     */
    @Query("""
            select  count(gt) + 1
            from    GameTurn gt
            where   gt.gameSession.id = :gameSessionId
            """)
    int findCurrentRound(@Param("gameSessionId") Long gameSessionId);

    /**
     * 세션에 포함되어 있는 GameTurn 목록을 조회한다.
     * @param gameSessionId
     * @return 타겟 세션에서 진행된 turn 목록
     */
    @Query("""
            select  gt
            from    GameTurn gt
            where   gt.gameSession.id = :gameSessionId
            """)
    List<GameTurn> findTurnsByGameSessionId(Long gameSessionId);
}
