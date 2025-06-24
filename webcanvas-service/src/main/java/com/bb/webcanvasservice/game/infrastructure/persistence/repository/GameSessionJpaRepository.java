package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.model.gameroom.GameSessionState;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameSessionJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameTurnJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
            where   gt.gameSessionEntity.id = :gameSessionId
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
            join fetch GameSessionJpaEntity gs
            on      gt.gameSessionEntity = gs
            where   gt.gameSessionEntity.id = :gameSessionId
            """)
    List<GameTurnJpaEntity> findTurnsByGameSessionId(@Param("gameSessionId") Long gameSessionId);

    @Query("""
          select  gs
          from    GameSessionJpaEntity gs
          where   gs.gameRoomEntity.id = :gameRoomId
          """)
    List<GameSessionJpaEntity> findGameSessionsByGameRoomId(@Param("gameRoomId") Long gameRoomId);

    /**
     * 게임 방 ID, 상태들로 게임 세션 조회
     * @param gameRoomId 게임 방 ID
     * @param activeStates 활성 상태
     */
    @Query(
            """
            select      gs
            from        GameSessionJpaEntity gs
            join fetch  GameRoomJpaEntity gr
            on          gs.gameRoomEntity = gr
            where       gs.gameRoomEntity.id = :gameRoomId
            and         gs.state in :activeStates
            """
    )
    Optional<GameSessionJpaEntity> findByGameRoomIdAndStates(@Param("gameRoomId") Long gameRoomId,
                                                             @Param("activeStates") List<GameSessionState> activeStates);

    /**
     * 게임 방 목록과 대상 상태를 필터링해 게임 세션 목록을 조회한다.
     *
     * @param gameRooms 게임 방 엔티티 목록
     * @param activeStates 활성 상태 목록
     * @return  게임 세션 엔티티 목록
     */
    @Query("""
           select       gs
           from         GameSessionJpaEntity gs
           join fetch   GameRoomJpaEntity gr
           on           gs.gameRoomEntity = gr
           where        gs.gameRoomEntity in :gameRooms
           and          gs.state in :activeStates
           """
    )
    List<GameSessionJpaEntity> findGameSessionsByGameRoomsAndStates(@Param("gameRooms") List<GameRoomJpaEntity> gameRooms, @Param("activeStates") List<GameSessionState> activeStates);
}
