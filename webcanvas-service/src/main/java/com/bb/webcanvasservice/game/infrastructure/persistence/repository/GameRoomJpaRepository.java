package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
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
            select exists (
                select  1
                from    GameRoomJpaEntity gr
                where   gr.state != 'CLOSED'
                and     gr.joinCode = :joinCode
            )
            """)
    boolean existsJoinCodeConflictOnActiveGameRoom(@Param("joinCode") String joinCode);

    /**
     * 입장 가능한 게임 방의 목록을 가져온다.
     *
     * 조건 1. GameRoom과 연관된 GameRoomParticipant의 수가 게임 방의 수용 인원 수보다 작거나 같아야함
     * 조건 2. GameRoom의 state가 enterableStates에 맞는 엔티티만 조회
     * @param gameRoomCapacity 게임 방 최대 정원
     * @param enterableStates 입장 가능한 방 상태
     * @param activeEntranceState 대상 게임방에 입장한 유저들의 유효한 입장 상태
     * @return 입장 가능한 게임 방 Entity 목록
     */
    @Query(
            """
            select  gr
            from    GameRoomJpaEntity gr
            where   gr.state in :enterableStates
            and     (
                        select  count(grp)
                        from    GameRoomParticipantJpaEntity grp
                        where   grp.gameRoomEntity.id = gr.id
                        and     grp.state = :activeEntranceState
                    ) < :gameRoomCapacity
            """
    )
    List<GameRoomJpaEntity> findGameRoomsByCapacityAndStateWithEntranceState(@Param("gameRoomCapacity") int gameRoomCapacity,
                                                                             @Param("enterableStates") List<GameRoomState> enterableStates,
                                                                             @Param("activeEntranceState") GameRoomParticipantState activeEntranceState);

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
           where    gr.joinCode =: joinCode
           and      gr.state = 'WAITING'
           """)
    Optional<GameRoomJpaEntity> findRoomWithJoinCodeForEnter(String joinCode);

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
    Optional<GameRoom> findByGameSessionId(@Param("gameSessionId") Long gameSessionId);
}
