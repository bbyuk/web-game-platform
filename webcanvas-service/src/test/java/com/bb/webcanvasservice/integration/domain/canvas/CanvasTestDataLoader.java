package com.bb.webcanvasservice.integration.domain.canvas;

import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.infrastructure.persistence.user.entity.UserJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.user.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Profile({"canvas-integration-test"})
public class CanvasTestDataLoader {

    @Autowired
    UserJpaRepository userJpaRepository;
    @Autowired
    GameRoomRepository gameRoomRepository;
    @Autowired
    GameRoomEntranceRepository gameRoomEntranceRepository;

    UserJpaEntity testUser1JpaEntity;
    UserJpaEntity testUser2JpaEntity;
    UserJpaEntity testUser3JpaEntity;
    GameRoom testGameRoom;
    final Stroke testStroke =
            new Stroke("FF5733", 5, List.of(
                    new Stroke.Point(0.46895640686922063, 0.24273767605633803),
                    new Stroke.Point(0.46895640686922063, 0.24273767605633803)
            ));

    @EventListener(ApplicationReadyEvent.class)
    public void setupTestData() {
        testUser1JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        testUser2JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        testUser3JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));

        System.out.println("testUser1 = " + testUser1JpaEntity.getId());
        System.out.println("testUser2 = " + testUser2JpaEntity.getId());
        System.out.println("testUser3 = " + testUser3JpaEntity.getId());

        testGameRoom = gameRoomRepository.save(new GameRoom(JoinCodeGenerator.generate(6)));

        gameRoomEntranceRepository.save(new GameRoomEntrance(testGameRoom, testUser1JpaEntity, "테스트 여우", GameRoomRole.HOST));
        gameRoomEntranceRepository.save(new GameRoomEntrance(testGameRoom, testUser2JpaEntity, "테스트 수달", GameRoomRole.GUEST));
        gameRoomEntranceRepository.save(new GameRoomEntrance(testGameRoom, testUser3JpaEntity,"테스트 늑대", GameRoomRole.GUEST));
    }

    @EventListener(ContextClosedEvent.class)
    public void clearTestData() {
        userJpaRepository.deleteAll();
        gameRoomRepository.deleteAll();
        gameRoomEntranceRepository.deleteAll();
    }
}
