package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.GameRoom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 게임 방의 persitence layer를 담당하는 레포지토리 클래스
 */
@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {


    @Query("""
            select      gre.gameRoom
            from        GameRoomEntrance gre
            join fetch GameRoom gr
            on          gre.gameRoom = gr
            where       gr.state != 'CLOSED'
            and         gre.user.id = :userId
            """)
    Optional<GameRoom> findNotClosedGameRoomByUserId(@Param("userId") Long userId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select exists (
                select 1
                from GameRoom gr
                where gr.state != 'CLOSED'
                and gr.joinCode = :joinCode
            )
            """)
    boolean existsJoinCodeConflictOnActiveGameRoom(@Param("joinCode") String joinCode);

    @Query("""
           select gr
           from GameRoom gr
           join fetch GameRoomEntrance gre
           on gre.gameRoom = gr
           where gr.id = :id
            """)
    Optional<GameRoom> findByIdWithEntrances(@Param("id") Long id);
}
