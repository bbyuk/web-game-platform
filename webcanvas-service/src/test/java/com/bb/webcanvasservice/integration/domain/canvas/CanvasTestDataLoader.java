package com.bb.webcanvasservice.integration.domain.canvas;

import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameRoomEntranceJpaEntity;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceRole;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomEntranceJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.user.entity.UserJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.user.repository.UserJpaRepository;
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
    GameRoomJpaRepository gameRoomRepository;
    @Autowired
    GameRoomEntranceJpaRepository gameRoomEntranceRepository;

    UserJpaEntity testUser1JpaEntity;
    UserJpaEntity testUser2JpaEntity;
    UserJpaEntity testUser3JpaEntity;
    GameRoomJpaEntity testGameRoom;
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

        testGameRoom = gameRoomRepository.save(new GameRoomJpaEntity(JoinCodeGenerator.generate(6)));

        gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(testGameRoom, testUser1JpaEntity, "테스트 여우", GameRoomEntranceRole.HOST));
        gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(testGameRoom, testUser2JpaEntity, "테스트 수달", GameRoomEntranceRole.GUEST));
        gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(testGameRoom, testUser3JpaEntity,"테스트 늑대", GameRoomEntranceRole.GUEST));
    }

    @EventListener(ContextClosedEvent.class)
    public void clearTestData() {
        userJpaRepository.deleteAll();
        gameRoomRepository.deleteAll();
        gameRoomEntranceRepository.deleteAll();
    }
}
