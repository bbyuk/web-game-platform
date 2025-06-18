package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomParticipantJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * User - GameRoom 엔티티간 조인 테이블 Entity인 GameRoomParticipant Entity의 persitence layer를 담당하는 레포지토리 클래스
 */
public interface GameRoomParticipantJpaRepository extends JpaRepository<GameRoomParticipantJpaEntity, Long> {

    /**
     * 게임 방 입장 여부 조회
     * @param userId 유저 ID
     * @return 대상 유저의 게임 방 입장 여부
     */
    @Query("""
            select exists(
                select      1
                from        GameRoomParticipantJpaEntity gre
                join        UserJpaEntity u
                on          gre.userEntity.id = u.id
                where       gre.userEntity.id = :userId
                and         gre.state = com.bb.webcanvasservice.game.domain.model.GameRoomEntranceState.WAITING
            )
            """)
    boolean existsGameRoomEntranceByUserId(@Param("userId") Long userId);

    /**
     * 입장한 방 찾기
     * @param userId 유저 ID
     * @return 대상 유저가 입장한 게임 방 입장 정보
     */
    @Query("""
           select       gre
           from         GameRoomParticipantJpaEntity gre
           join fetch   UserJpaEntity u
           on           gre.userEntity.id = u.id
           where        gre.userEntity.id = :userId
           and          gre.state = 'WAITING'
           """)
    Optional<GameRoomParticipantJpaEntity> findByUserId(@Param("userId") Long userId);

    /**
     * 게임 방 ID로 해당 게임 방에 입장한 정보 조회 (비관적 락 적용)
     *
     * 250430 - 입장한 순서대로 정렬 추가
     * @param gameRoomId 게임 방 ID
     * @return 대상 게임 방의 입장 목록
     */
    @Query(
            """
            select      gre
            from        GameRoomParticipantJpaEntity gre
            join fetch  UserJpaEntity u on gre.userEntity.id = u.id
            join fetch  GameRoomJpaEntity gr on gre.gameRoomEntity.id = gr.id
            where       gre.gameRoomEntity.id = :gameRoomId
            and         gre.state = com.bb.webcanvasservice.game.domain.model.GameRoomEntranceState.WAITING
            order by    gre.id asc
            """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<GameRoomParticipantJpaEntity> findGameRoomEntrancesByGameRoomIdWithLock(@Param("gameRoomId") Long gameRoomId);

    /**
     * 유저 ID로 현재 입장한 게임 방의 입장 정보 조회
     * @param userId 유저 ID
     * @return 대상 유저의 게임 방 입장 정보
     */
    @Query(
            """
            select      gre
            from        GameRoomParticipantJpaEntity gre
            join fetch  UserJpaEntity u on gre.userEntity.id = u.id
            join fetch  GameRoomJpaEntity gr on gre.gameRoomEntity.id = gr.id
            where       gre.userEntity.id = :userId
            and         gre.state in :gameRoomParticipantStates
            """
    )
    Optional<GameRoomParticipantJpaEntity> findGameRoomEntranceByUserIdAndGameRoomStates(@Param("userId") Long userId, @Param("gameRoomParticipantStates") List<GameRoomParticipantState> gameRoomParticipantStates);

    /**
     * 게임 방 ID와 상태로 GameRoomParticipant 목록을 조회한다.
     * @param gameRoomId 게임 방 ID
     * @param gameRoomParticipantState 조회 필터 게임 방 입장 상태
     * @return 게임 방 입장 정보 목록
     */
    @Query("""
            select      gre
            from        GameRoomParticipantJpaEntity gre
            join fetch  UserJpaEntity u on gre.userEntity.id = u.id
            join fetch  GameRoomJpaEntity gr on gre.gameRoomEntity.id = gr.id
            where       gre.gameRoomEntity.id = :gameRoomId
            and         gre.state = :gameRoomParticipantState
            order by    gre.id asc
            """)
    List<GameRoomParticipantJpaEntity> findGameRoomEntrancesByGameRoomIdAndState(@Param("gameRoomId") Long gameRoomId, @Param("gameRoomParticipantState") GameRoomParticipantState gameRoomParticipantState);

    /**
     * 방에 입장한 수
     * @param gameRoomId 게임 방 ID
     * @param gameRoomParticipantState 게임 방에 입장해있는 상태 코드
     * @return 게임 방 입장 수
     */
    @Query(
            """
            select      count(gre)
            from        GameRoomParticipantJpaEntity gre
            where       gre.gameRoomEntity.id = :gameRoomId
            and         gre.state in (
                                        com.bb.webcanvasservice.game.domain.model.GameRoomEntranceState.WAITING,
                                        com.bb.webcanvasservice.game.domain.model.GameRoomEntranceState.PLAYING
                                     )
            order by    gre.id asc
            """
    )
    long findGameRoomEntranceCountByGameRoomIdAndState(@Param("gameRoomId") Long gameRoomId, @Param("gameRoomParticipantState") GameRoomParticipantState gameRoomParticipantState);

    /**
     * 게임 방 ID와 상태들로 GameRoomParticipant 목록을 조회한다.
     * @param gameRoomId 게임 방 ID
     * @param gameRoomParticipantStates 조회 필터 - 게임 방 입장 상태 목록
     * @return 게임 방 입장 목록
     */
    @Query("""
            select      gre
            from        GameRoomParticipantJpaEntity gre
            join fetch  UserJpaEntity u on gre.userEntity.id = u.id
            join fetch  GameRoomJpaEntity gr on gre.gameRoomEntity.id = gr.id
            where       gre.gameRoomEntity.id = :gameRoomId
            and         gre.state in :gameRoomParticipantStates
            order by    gre.id asc
            """)
    List<GameRoomParticipantJpaEntity> findGameRoomEntrancesByGameRoomIdAndStates(@Param("gameRoomId") Long gameRoomId, @Param("gameRoomParticipantStates") List<GameRoomParticipantState> gameRoomParticipantStates);
}
