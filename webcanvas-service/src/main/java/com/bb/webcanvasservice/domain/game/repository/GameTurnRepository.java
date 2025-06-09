package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 게임 턴 관련 Repository
 */
public interface GameTurnRepository extends JpaRepository<GameTurn, Long>, GameTurnCustomRepository {

    /**
     * 게임 세션 ID로 해당 세션에 포함된 게임 턴 목록을 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 턴 목록
     */
    @Query("""
            select  gt
            from    GameTurn gt
            where   gt.gameSession.id = :gameSessionId
            """)
    List<GameTurn> findTurnsByGameSessionId(@Param("gameSessionId") Long gameSessionId);

    /**
     * 게임 세션 ID로 해당 세션에 포함된 게임 턴 수를 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 턴 수
     */
    @Query("""
            select  count(gt)
            from    GameTurn gt
            where   gt.gameSession.id = :gameSessionId
            """)
    long findTurnCountByGameSessionId(@Param("gameSessionId") Long gameSessionId);
}
