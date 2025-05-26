package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    int findCurrentRound(Long gameSessionId);
}
