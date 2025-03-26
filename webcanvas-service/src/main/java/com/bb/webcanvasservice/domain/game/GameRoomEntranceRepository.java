package com.bb.webcanvasservice.domain.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User - GameRoom 엔티티간 조인 테이블 Entity인 GameRoomEntrance Entity의 persitence layer를 담당하는 레포지토리 클래스
 */
@Repository
public interface GameRoomEntranceRepository extends JpaRepository<GameRoomEntrance, Long> {
}
