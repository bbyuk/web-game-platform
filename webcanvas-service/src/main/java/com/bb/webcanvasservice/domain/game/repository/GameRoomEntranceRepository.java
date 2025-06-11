package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * User - GameRoom 엔티티간 조인 테이블 Entity인 GameRoomEntrance Entity의 persitence layer를 담당하는 레포지토리 클래스
 */
public interface GameRoomEntranceRepository extends JpaRepository<GameRoomEntrance, Long>, GameRoomEntranceCustomRepository {

    /**
     * 게임 방 입장 여부 조회
     * @param userId
     * @return exists
     */
    @Query("""
            select exists(
                select  1
                from    GameRoomEntrance gre
                join    UserJpaEntity u
                on      gre.user = u
                where   gre.user.id = :userId
                and     gre.state = 'WAITING'
            )
            """)
    boolean existsGameRoomEntranceByUserId(@Param("userId") Long userId);

    /**
     * 입장한 방 찾기
     * @param userId
     * @return gameRoomEntrance
     */
    @Query("""
           select  gre
           from    GameRoomEntrance gre
           join    UserJpaEntity u
           on      gre.user = u
           where   gre.user.id = :userId
           and     gre.state = 'WAITING'
           """)
    Optional<GameRoomEntrance> findByUserId(@Param("userId") Long userId);

    /**
     * 게임 방 ID로 해당 게임 방에 입장한 정보 조회 (비관적 락 적용)
     *
     * 250430 - 입장한 순서대로 정렬 추가
     * @param gameRoomId
     * @return gameRoomEntrances
     */
    @Query(
            """
            select      gre
            from        GameRoomEntrance gre
            join fetch  UserJpaEntity u on gre.user = u
            join fetch  GameRoom gr on gre.gameRoom = gr
            where       gre.gameRoom.id = :gameRoomId
            and         gre.state = 'WAITING'
            order by    gre.id asc
            """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdWithLock(@Param("gameRoomId") Long gameRoomId);

    /**
     * 유저 ID로 현재 입장한 게임 방의 입장 정보 조회
     * @param userId
     * @return
     */
    @Query(
            """
            select  gre
            from    GameRoomEntrance gre
            join    fetch UserJpaEntity u on gre.user = u
            join    fetch GameRoom gr on gre.gameRoom = gr
            where   gre.user.id = :userId
            and     gre.state in :gameRoomEntranceStates
            """
    )
    Optional<GameRoomEntrance> findGameRoomEntranceByUserId(@Param("userId") Long userId, @Param("gameRoomEntranceStates") List<GameRoomEntranceState> gameRoomEntranceStates);

    /**
     * 게임 방 ID와 상태로 GameRoomEntrance 목록을 조회한다.
     * @param gameRoomId
     * @param gameRoomEntranceState
     * @return
     */
    @Query("""
            select      gre
            from        GameRoomEntrance gre
            join fetch  UserJpaEntity u on gre.user = u
            join fetch  GameRoom gr on gre.gameRoom = gr
            where       gre.gameRoom.id = :gameRoomId
            and         gre.state = :gameRoomEntranceState
            order by    gre.id asc
            """)
    List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdAndState(@Param("gameRoomId") Long gameRoomId, @Param("gameRoomEntranceState") GameRoomEntranceState gameRoomEntranceState);

    @Query(
            """
            select      count(gre)
            from        GameRoomEntrance gre
            where       gre.gameRoom.id = :gameRoomId
            and         gre.state = :gameRoomEntranceState
            order by    gre.id asc
            """
    )
    long findGameRoomEntranceCountByGameRoomIdAndState(@Param("gameRoomId") Long gameRoomId, @Param("gameRoomEntranceState") GameRoomEntranceState gameRoomEntranceState);

    /**
     * 게임 방 ID와 상태들로 GameRoomEntrance 목록을 조회한다.
     * @param gameRoomId
     * @param gameRoomEntranceStates
     * @return
     */
    @Query("""
            select      gre
            from        GameRoomEntrance gre
            join fetch  UserJpaEntity u on gre.user = u
            join fetch  GameRoom gr on gre.gameRoom = gr
            where       gre.gameRoom.id = :gameRoomId
            and         gre.state in :gameRoomEntranceStates
            order by    gre.id asc
            """)
    List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdAndStates(@Param("gameRoomId") Long gameRoomId, @Param("gameRoomEntranceStates") List<GameRoomEntranceState> gameRoomEntranceStates);
}
