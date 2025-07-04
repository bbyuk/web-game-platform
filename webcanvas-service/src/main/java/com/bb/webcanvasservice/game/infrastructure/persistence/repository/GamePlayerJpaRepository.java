package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GamePlayerJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 게임 플레이어 JPA Repository
 */
public interface GamePlayerJpaRepository extends JpaRepository<GamePlayerJpaEntity, Long> {

    /**
     * GameSession ID로 해당 세션에 포함된 게임 플레이어 목록을 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 게임 플레이어 목록
     */
    @Query("""
            select  gp
            from    GamePlayerJpaEntity gp
            where   gp.gameSessionEntity.id = :gameSessionId
            """)
    @EntityGraph(attributePaths = {"gameSessionEntity"})
    List<GamePlayerJpaEntity> findByGameSessionId(@Param("gameSessionId") Long gameSessionId);
}
