package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GamePlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 게임 플레이 이력 레포지토리
 */
public interface GamePlayHistoryRepository extends JpaRepository<GamePlayHistory, Long> {

    /**
     * GameSessionId로 게임 플레이 유저 조회
     * @param gameSessionId
     * @return
     */
    @Query("""
            select  gph
            from    GamePlayHistory gph
            where   gph.gameSession.id = :gameSessionId
            """)
    List<GamePlayHistory> findByGameSessionId(@Param("gameSessionId") Long gameSessionId);
}
