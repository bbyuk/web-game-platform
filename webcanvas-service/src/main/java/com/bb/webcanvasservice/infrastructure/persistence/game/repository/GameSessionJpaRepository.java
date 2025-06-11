package com.bb.webcanvasservice.infrastructure.persistence.game.repository;

import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameSessionJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameTurnJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 게임 세션 레포지토리
 */
public interface GameSessionJpaRepository extends JpaRepository<GameSessionJpaEntity, Long> {

    /**
     * 현재 라운드를 조회한다.
     * @param gameSessionId
     * @return
     */
    @Query("""
            select  count(gt) + 1
            from    GameTurnJpaEntity gt
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
            from    GameTurnJpaEntity gt
            where   gt.gameSession.id = :gameSessionId
            """)
    List<GameTurnJpaEntity> findTurnsByGameSessionId(@Param("gameSessionId") Long gameSessionId);

    @Query("""
          select  gs
          from    GameSessionJpaEntity gs
          where   gs.gameRoom.id = :gameRoomId
          """)
    List<GameSessionJpaEntity> findGameSessionsByGameRoomId(@Param("gameRoomId") Long gameRoomId);
}
