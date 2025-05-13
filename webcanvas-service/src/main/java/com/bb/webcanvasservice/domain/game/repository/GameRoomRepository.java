package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게임 방의 persitence layer를 담당하는 레포지토리 클래스
 */
@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {

    /**
     * 현재 입장해있는 방을 조회한다.
     * @param userId 유저ID
     * @return gameRoom 현재 입장해있는 방
     */
    @Query("""
            select      gre.gameRoom
            from        GameRoomEntrance gre
            join fetch  GameRoom gr
            on          gre.gameRoom = gr
            where       gr.state != 'CLOSED'
            and         gre.user.id = :userId
            """)
    Optional<GameRoom> findNotClosedGameRoomByUserId(@Param("userId") Long userId);

    /**
     * 현재 입장 가능한 방들 중 joinCode의 충돌이 있는지 여부를 비관적 락을 걸어 확인한다.
     * @param joinCode
     * @return
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select exists (
                select  1
                from    GameRoom gr
                where   gr.state != 'CLOSED'
                and     gr.joinCode = :joinCode
            )
            """)
    boolean existsJoinCodeConflictOnActiveGameRoom(@Param("joinCode") String joinCode);

    /**
     * GameRoom 상태로 게엠 방을 조회한다.
     * @param state
     * @return
     */
    @Query("""
            select  gr
            from    GameRoom gr
            where   gr.state = :state
            """)
    List<GameRoom> findByState(@Param("state") GameRoomState state);

    /**
     * 입장 가능한 게임 방의 목록을 가져온다.
     *
     * 조건 1. GameRoom과 연관된 GameRoomEntrance의 수가 게임 방의 수용 인원 수보다 작거나 같아야함
     * 조건 2. GameRoom의 state가 enterableStates에 맞는 엔티티만 조회
     * @param gameRoomCapacity
     * @param enterableStates
     * @return
     */
    @Query(
            """
            select      gre.gameRoom
            from        GameRoomEntrance gre
            where       gre.state = :activeEntranceState
            and         gre.gameRoom.state in :enterableStates
            group by    gre.gameRoom
            having      count(gre) < :gameRoomCapacity
            """
    )
    List<GameRoom> findGameRoomsByCapacityAndStateWithEntranceState(@Param("gameRoomCapacity") int gameRoomCapacity,
                                                                    @Param("enterableStates") List<GameRoomState> enterableStates,
                                                                    @Param("activeEntranceState")GameRoomEntranceState activeEntranceState);

    /**
     * JoinCode로 입장할 방 조회
     * GameRoom.state = 'WAITING' 이어야 한다.
     * 
     * @param joinCode
     * @return
     */
    @Query("""
           select   gr
           from     GameRoom gr
           where    gr.joinCode =: joinCode
           and      gr.state = 'WAITING'
           """)
    Optional<GameRoom> findRoomWithJoinCodeForEnter(String joinCode);
}
