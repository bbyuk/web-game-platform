package com.bb.webcanvasservice.infrastructure.persistence.game.repository;

import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GamePlayHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 게임 플레이 이력 레포지토리
 */
public interface GamePlayHistoryJpaRepository extends JpaRepository<GamePlayHistoryJpaEntity, Long> {


    /**
     * GameSessionId로 게임 플레이 유저 조회
     * @param gameSessionId
     * @return
     */
    @Query("""
            select  gph
            from    GamePlayHistoryJpaEntity gph
            where   gph.gameSession.id = :gameSessionId
            """)
    List<GamePlayHistoryJpaEntity> findByGameSessionId(@Param("gameSessionId") Long gameSessionId);
}
