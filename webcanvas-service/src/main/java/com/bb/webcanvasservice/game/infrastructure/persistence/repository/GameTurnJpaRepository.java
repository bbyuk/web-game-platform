package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.model.session.GameTurnState;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameTurnJpaEntity;
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
            where   gt.gameSessionEntity.id = :gameSessionId
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
            where   gt.gameSessionEntity.id = :gameSessionId
            """)
    long findTurnCountByGameSessionId(@Param("gameSessionId") Long gameSessionId);


    /**
     * 게임 세션 ID와 상태 코드 목록으로 필터된 게임 턴 수를 조회한다.
     * @param gameSessionId 대상 게임 세션 ID
     * @param states 턴 상태 코드 목록
     * @return 턴수
     */
    @Query("""
           select   count(gt)
           from     GameTurnJpaEntity gt
           where    gt.gameSessionEntity.id = :gameSessionId
           and      gt.state in :states
           """)
    long findTurnCountByGameSessionIdAndStates(@Param("gameSessionId") Long gameSessionId, @Param("states") List<GameTurnState> states);
}
