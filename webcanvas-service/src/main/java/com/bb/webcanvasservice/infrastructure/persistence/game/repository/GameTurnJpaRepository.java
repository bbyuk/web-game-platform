package com.bb.webcanvasservice.infrastructure.persistence.game.repository;

import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameTurnJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 게임 턴 관련 Repository
 */
public interface GameTurnJpaRepository extends JpaRepository<GameTurnJpaEntity, Long> {

    /**
     * 게임 세션 ID로 해당 세션에 포함된 게임 턴 목록을 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 턴 목록
     */
    @Query("""
            select  gt
            from    GameTurnJpaEntity gt
            where   gt.gameSession.id = :gameSessionId
            """)
    List<GameTurnJpaEntity> findTurnsByGameSessionId(@Param("gameSessionId") Long gameSessionId);

    /**
     * 게임 세션 ID로 해당 세션에 포함된 게임 턴 수를 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 턴 수
     */
    @Query("""
            select  count(gt)
            from    GameTurnJpaEntity gt
            where   gt.gameSession.id = :gameSessionId
            """)
    long findTurnCountByGameSessionId(@Param("gameSessionId") Long gameSessionId);
}
