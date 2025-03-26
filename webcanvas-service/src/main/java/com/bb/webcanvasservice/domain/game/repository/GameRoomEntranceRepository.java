package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * User - GameRoom 엔티티간 조인 테이블 Entity인 GameRoomEntrance Entity의 persitence layer를 담당하는 레포지토리 클래스
 */
@Repository
public interface GameRoomEntranceRepository extends JpaRepository<GameRoomEntrance, Long> {

    @Query("""
            select exists(
                select 1
                from GameRoomEntrance gre
                join User u
                on gre.user = u
                where gre.user.id = :userId
            )
            """)
    boolean existsGameRoomEntranceByUserId(@Param("userId") Long userId);
}
