package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User - GameRoom 엔티티간 조인 테이블 Entity인 GameRoomEntrance Entity의 persitence layer를 담당하는 레포지토리 클래스
 */
@Repository
public interface GameRoomEntranceRepository extends JpaRepository<GameRoomEntrance, Long> {

    /**
     * 게임 방 입장 여부 조회
     * @param userId
     * @return exists
     */
    @Query("""
            select exists(
                select  1
                from    GameRoomEntrance gre
                join    User u
                on      gre.user = u
                where   gre.user.id = :userId
                and     gre.state = 'ACTIVE'
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
           join    User u
           on      gre.user = u
           where   gre.user.id = :userId
           and     gre.state = 'ACTIVE'
           """)
    Optional<GameRoomEntrance> findByUserId(@Param("userId") Long userId);

    /**
     * 게임 방 ID로 해당 게임 방에 입장한 정보 조회
     *
     * 250430 - 입장한 순서대로 정렬 추가
     * @param gameRoomId
     * @return gameRoomEntrances
     */
    @Query(
            """
            select      gre
            from        GameRoomEntrance gre
            join fetch  User u on gre.user = u
            join fetch  GameRoom gr on gre.gameRoom = gr
            where       gre.gameRoom.id = :gameRoomId
            and         gre.state = 'ACTIVE'
            order by    gre.id asc
            """
    )
    List<GameRoomEntrance> findGameRoomEntrancesByGameRoomId(@Param("gameRoomId") Long gameRoomId);

    /**
     * 유저 ID로 현재 입장한 게임 방의 입장 정보 조회
     * @param userId
     * @return
     */
    @Query(
            """
            select  gre
            from    GameRoomEntrance gre
            join    fetch User u on gre.user = u
            join    fetch GameRoom gr on gre.gameRoom = gr
            where   gre.user.id = :userId
            and     gre.state = 'ACTIVE'
            """
    )
    Optional<GameRoomEntrance> findGameRoomEntranceByUserId(@Param("userId") Long userId);

    /**
     * gameRoom에 입장되어 있는지 여부를 조회한다.
     * @param gameRoomId
     * @param userId
     * @return
     */
    @Query(
            """
            select exists (
                 select 1
                 from   GameRoomEntrance gre
                 where  gre.gameRoom.id = :gameRoomId
                 and    gre.user.id = :userId
                 and    gre.state = 'ACTIVE'
            )
            """
    )
    boolean existsActiveEntrance(@Param("gameRoomId") Long gameRoomId, @Param("userId") Long userId);
}
