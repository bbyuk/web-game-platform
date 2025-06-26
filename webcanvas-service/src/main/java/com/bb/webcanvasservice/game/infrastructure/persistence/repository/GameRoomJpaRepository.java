package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomState;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 게임 방의 persitence layer를 담당하는 레포지토리 클래스
 */
public interface GameRoomJpaRepository extends JpaRepository<GameRoomJpaEntity, Long> {

    /**
     * 현재 입장해있는 방을 조회한다.
     * @param userId 유저ID
     * @return 현재 입장해있는 방
     */
    @Query("""
            select      gr
            from        GameRoomJpaEntity gr
            join fetch  GameRoomParticipantJpaEntity grp
            on          grp.gameRoomEntity.id = gr.id
            where       gr.state in (:joinedStates)
            and         grp.userEntity.id = :userId
            """)
    Optional<GameRoomJpaEntity> findCurrentJoinedGameRoomByUserId(@Param("userId") Long userId, @Param("joinedStates") List<GameRoomParticipantState> joinedStates);

    /**
     * 현재 입장 가능한 방들 중 joinCode의 충돌이 있는지 여부를 비관적 락을 걸어 확인한다.
     * @param joinCode 입장 코드
     * @return 입장 가능 여부
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select  gr
            from    GameRoomJpaEntity gr
            where   gr.joinCode =   :joinCode
            and     gr.state    in  :activeStates
            """)
    List<GameRoomJpaEntity> findGameRoomByJoinCodeAndActiveStatesWithLock(@Param("joinCode") String joinCode, @Param("activeStates")List<GameRoomState> activeStates);

    /**
     * 게임 방과 게임 방 입장자 상태, 게임 방의 정원에 맞는 게임 방의 목록을 가져온다.
     *
     * @param gameRoomCapacity 게임 방 최대 정원
     * @param gameRoomState 필터 게임 방 상태
     * @param gameRoomParticipantState 필터 게임 방 입장자 상태
     * @return 게임 방 Entity 목록
     */
    @Query(
            """
            select  gr
            from    GameRoomJpaEntity gr
            where   gr.state = :gameRoomState
            and     (
                        select  count(grp)
                        from    GameRoomParticipantJpaEntity grp
                        where   grp.gameRoomEntity.id = gr.id
                        and     grp.state = :gameRoomParticipantState
                    ) < :gameRoomCapacity
            """
    )
    List<GameRoomJpaEntity> findGameRoomsByCapacityAndStateAndGameRoomParticipantState(@Param("gameRoomCapacity") int gameRoomCapacity,
                                                                                       @Param("gameRoomState") GameRoomState gameRoomState,
                                                                                       @Param("gameRoomParticipantState") GameRoomParticipantState gameRoomParticipantState);

    /**
     * GameRoom 상태로 게엠 방을 조회한다.
     * @param state
     * @return
     */
    @Query("""
            select  gr
            from    GameRoomJpaEntity gr
            where   gr.state = :state
            """)
    List<GameRoomJpaEntity> findByState(@Param("state") GameRoomState state);

    /**
     * JoinCode로 입장할 방 조회
     * GameRoom.state = 'WAITING' 이어야 한다.
     * 
     * @param joinCode
     * @return 입장할 방 Entity
     */
    @Query("""
           select   gr
           from     GameRoomJpaEntity gr
           where    gr.joinCode = :joinCode
           and      gr.state = :state
           """)
    Optional<GameRoomJpaEntity> findGameRoomByJoinCodeAndState(@Param("joinCode") String joinCode, @Param("state") GameRoomState state);

    /**
     * 게임 방 입장자 ID로 게임 방을 조회한다.
     * @param gameRoomParticipantId 게임 방 입장자 ID
     * @return 게잉 방 JPA Entity
     */
    @Query("""
            select  grp.gameRoomEntity
            from    GameRoomParticipantJpaEntity grp
            where   grp.id = :gameRoomParticipantId
            """)
    Optional<GameRoomJpaEntity> findByGameRoomParticipantId(@Param("gameRoomParticipantId") Long gameRoomParticipantId);

    /**
     * 게임 세션 ID로 해당 게임세션이 진행되는 게임 방 객체를 리턴한다.
     * @param gameSessionId 게임 세션 ID
     * @return 게임 방 객체
     */
    @Query(
            """
            select  gs.gameRoomEntity
            from    GameSessionJpaEntity gs
            where   gs.id = :gameSessionId
            """
    )
    Optional<GameRoomJpaEntity> findByGameSessionId(@Param("gameSessionId") Long gameSessionId);
}
