package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomState;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomParticipantJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
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
                and         gre.state = com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState.WAITING
            )
            """)
    boolean existsGameRoomParticipantByUserId(@Param("userId") Long userId);

    /**
     * 입장한 방 찾기
     * @param userId 유저 ID
     * @return 대상 유저가 입장한 게임 방 입장 정보
     */
    @Query("""
           select       grp
           from         GameRoomParticipantJpaEntity grp
           join fetch   grp.userEntity u
           where        grp.userEntity.id = :userId
           and          grp.state = 'WAITING'
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
            select      grp
            from        GameRoomParticipantJpaEntity grp
            join fetch  grp.userEntity u
            join fetch  grp.gameRoomEntity gr
            where       grp.gameRoomEntity.id = :gameRoomId
            and         grp.state = com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState.WAITING
            order by    grp.id asc
            """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<GameRoomParticipantJpaEntity> findGameRoomParticipantsByGameRoomIdWithLock(@Param("gameRoomId") Long gameRoomId);

    /**
     * 유저 ID로 현재 입장한 게임 방의 입장 정보 조회
     * @param userId 유저 ID
     * @return 대상 유저의 게임 방 입장 정보
     */
    @Query(
            """
            select      grp
            from        GameRoomParticipantJpaEntity grp
            join fetch  grp.userEntity u
            join fetch  grp.gameRoomEntity gr
            where       grp.userEntity.id = :userId
            and         grp.state in :gameRoomParticipantStates
            """
    )
    Optional<GameRoomParticipantJpaEntity> findGameRoomParticipantByUserIdAndGameRoomStates(@Param("userId") Long userId, @Param("gameRoomParticipantStates") List<GameRoomParticipantState> gameRoomParticipantStates);

    /**
     * 게임 방 ID와 상태로 GameRoomParticipant 목록을 조회한다.
     * @param gameRoomId 게임 방 ID
     * @param gameRoomParticipantState 조회 필터 게임 방 입장 상태
     * @return 게임 방 입장 정보 목록
     */
    @Query("""
            select      grp
            from        GameRoomParticipantJpaEntity grp
            join fetch  grp.userEntity u
            join fetch  grp.gameRoomEntity gr
            where       grp.gameRoomEntity.id = :gameRoomId
            and         grp.state = :gameRoomParticipantState
            order by    grp.id asc
            """)
    List<GameRoomParticipantJpaEntity> findGameRoomParticipantsByGameRoomIdAndState(@Param("gameRoomId") Long gameRoomId, @Param("gameRoomParticipantState") GameRoomParticipantState gameRoomParticipantState);

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
                                        com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState.WAITING,
                                        com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState.PLAYING
                                     )
            order by    gre.id asc
            """
    )
    long findGameRoomParticipantCountByGameRoomIdAndState(@Param("gameRoomId") Long gameRoomId, @Param("gameRoomParticipantState") GameRoomParticipantState gameRoomParticipantState);

    /**
     * 게임 방 ID와 상태들로 GameRoomParticipant 목록을 조회한다.
     * @param gameRoomId 게임 방 ID
     * @param gameRoomParticipantStates 조회 필터 - 게임 방 입장 상태 목록
     * @return 게임 방 입장 목록
     */
    @Query("""
            select      grp
            from        GameRoomParticipantJpaEntity grp
            join fetch  grp.userEntity u
            join fetch  grp.gameRoomEntity gr
            where       grp.gameRoomEntity.id = :gameRoomId
            and         grp.state in :gameRoomParticipantStates
            order by    grp.id asc
            """)
    List<GameRoomParticipantJpaEntity> findGameRoomParticipantsByGameRoomIdAndStates(@Param("gameRoomId") Long gameRoomId, @Param("gameRoomParticipantStates") List<GameRoomParticipantState> gameRoomParticipantStates);

    /**
     * 게임 방에 해당하는 게임 방 입장자들을 모두 가져온다.
     * @param gameRoomJpaEntities gameRoomJpa 엔티티 목록
     * @return 게임 방 입장자 entity 목록
     */
    @Query("""
            select      grp
            from        GameRoomParticipantJpaEntity grp
            join fetch  grp.gameRoomEntity gr
            where       grp.gameRoomEntity in :gameRooms
           """)
    List<GameRoomParticipantJpaEntity> findGameRoomParticipantsByGameRooms(@Param("gameRooms") List<GameRoomJpaEntity> gameRoomJpaEntities);


    /**
     * 게임 방 상태와 게임 방 입장자 상태로 게임 방 입장자 목록을 게임 방 entity와 함께 조회한다.
     *
     * @param gameRoomIds 게임방 id 리스트
     * @param gameRoomParticipantState 게임 방 입장자 상태
     * @param gameRoomState 게임 방 상태
     * @return
     */
    @Query(
            """
            select      grp
            from        GameRoomParticipantJpaEntity grp
            where       grp.gameRoomEntity.id in :gameRoomIds
            and         grp.state = :gameRoomParticipantState
            and         grp.gameRoomEntity.state = :gameRoomState
            """
    )
    @EntityGraph(attributePaths = { "gameRoomEntity" })
    List<GameRoomParticipantJpaEntity> findGameRoomParticipantsByGameRoomParticipantStateAndGameRoomState(
            @Param("gameRoomIds") List<Long> gameRoomIds,
            @Param("gameRoomParticipantState") GameRoomParticipantState gameRoomParticipantState,
            @Param("gameRoomState") GameRoomState gameRoomState);
}
