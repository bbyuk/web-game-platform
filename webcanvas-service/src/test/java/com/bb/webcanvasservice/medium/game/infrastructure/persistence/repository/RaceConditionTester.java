package com.bb.webcanvasservice.medium.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.GameRoomJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Race Condition을 테스트 하기 위해 트랜잭션 처리의 책임을 갖는 테스트 컴포넌트
 */
@TestComponent
public class RaceConditionTester {

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private GameRoomJpaRepository gameRoomJpaRepository;

    /**
     * JoinCode 충돌 체크 테스트를 위한 Race Condition 발생 로직
     * @param joinCode
     * @param roomCapacity
     * @param joinCodeUsingCount
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void joinCodeRaceCondition(String joinCode, int roomCapacity, AtomicInteger joinCodeUsingCount) {
        GameRoom gameRoom = GameRoom.create(joinCode, roomCapacity);


        System.out.println("Thread " + Thread.currentThread().getName() + " select");

        if (!gameRoomRepository.existsJoinCodeConflictOnActiveGameRoom(joinCode)) {
            gameRoomRepository.save(gameRoom);
            gameRoomJpaRepository.flush();
            System.out.println("Thread " + Thread.currentThread().getName() + " saved");

            joinCodeUsingCount.incrementAndGet();
        }
    }
}
