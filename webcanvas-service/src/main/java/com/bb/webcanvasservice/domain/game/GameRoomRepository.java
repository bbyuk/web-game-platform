package com.bb.webcanvasservice.domain.game;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {


    @Query("""
            select      gre.gameRoom
            from        GameRoomEntrance gre
            join fetch GameRoom gr
            on          gre.gameRoom = gr
            where       gr.status != :gameRoomStatus
            and         gre.user.userToken = :userToken
            """)
    Optional<GameRoom> findByGameRoomStatusNotMatchedAndUserToken(@Param("gameRoomStatus") GameRoomStatus gameRoomStatus, @Param("userToken") String userToken);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select  gr
            from    GameRoom gr
            where   gr.status != :gameRoomStatus
            and     gr.joinCode = :joinCode
            """)
    Optional<GameRoom> findByGameRoomStatusNotMatchedAndJoinCode(@Param("gameRoomStatus") GameRoomStatus gameRoomStatus, @Param("joinCode") String joinCode);
}
