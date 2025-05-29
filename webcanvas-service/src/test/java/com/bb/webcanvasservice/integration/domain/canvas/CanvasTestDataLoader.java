package com.bb.webcanvasservice.integration.domain.canvas;

import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.entity.User;
import com.bb.webcanvasservice.domain.user.repository.UserRepository;
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
    UserRepository userRepository;
    @Autowired
    GameRoomRepository gameRoomRepository;
    @Autowired
    GameRoomEntranceRepository gameRoomEntranceRepository;

    User testUser1;
    User testUser2;
    User testUser3;
    GameRoom testGameRoom;
    final Stroke testStroke =
            new Stroke("FF5733", 5, List.of(
                    new Stroke.Point(0.46895640686922063, 0.24273767605633803),
                    new Stroke.Point(0.46895640686922063, 0.24273767605633803)
            ));

    @EventListener(ApplicationReadyEvent.class)
    public void setupTestData() {
        testUser1 = userRepository.save(new User(UUID.randomUUID().toString()));
        testUser2 = userRepository.save(new User(UUID.randomUUID().toString()));
        testUser3 = userRepository.save(new User(UUID.randomUUID().toString()));

        System.out.println("testUser1 = " + testUser1.getId());
        System.out.println("testUser2 = " + testUser2.getId());
        System.out.println("testUser3 = " + testUser3.getId());

        testGameRoom = gameRoomRepository.save(new GameRoom(JoinCodeGenerator.generate(6)));

        gameRoomEntranceRepository.save(new GameRoomEntrance(testGameRoom, testUser1, "테스트 여우", GameRoomRole.HOST));
        gameRoomEntranceRepository.save(new GameRoomEntrance(testGameRoom, testUser2, "테스트 수달", GameRoomRole.GUEST));
        gameRoomEntranceRepository.save(new GameRoomEntrance(testGameRoom, testUser3,"테스트 늑대", GameRoomRole.GUEST));
    }

    @EventListener(ContextClosedEvent.class)
    public void clearTestData() {
        userRepository.deleteAll();
        gameRoomRepository.deleteAll();
        gameRoomEntranceRepository.deleteAll();
    }
}
