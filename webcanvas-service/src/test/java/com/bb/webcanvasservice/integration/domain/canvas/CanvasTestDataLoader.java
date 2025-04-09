package com.bb.webcanvasservice.integration.domain.canvas;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.canvas.dto.Point;
import com.bb.webcanvasservice.domain.canvas.dto.Stroke;
import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
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
    final Stroke testStroke = Stroke.builder()
            .color("FF5733")  // 예제 색상 (주황빛 빨강)
            .lineWidth(5)
            .points(List.of(
                    Point.builder()
                            .x(10)
                            .y(20)
                            .build(),
                    Point.builder()
                            .x(20)
                            .y(30)
                            .build(),
                    Point.builder()
                            .x(30)
                            .y(40)
                            .build()
            ))
            .build();

    @EventListener(ApplicationReadyEvent.class)
    public void setUpTestData() {
        testUser1 = userRepository.save(new User(UUID.randomUUID().toString()));
        testUser2 = userRepository.save(new User(UUID.randomUUID().toString()));
        testUser3 = userRepository.save(new User(UUID.randomUUID().toString()));

        System.out.println("testUser1 = " + testUser1.getId());
        System.out.println("testUser2 = " + testUser2.getId());
        System.out.println("testUser3 = " + testUser3.getId());

        testGameRoom = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(6)));

        gameRoomEntranceRepository.save(new GameRoomEntrance(testGameRoom, testUser1));
        gameRoomEntranceRepository.save(new GameRoomEntrance(testGameRoom, testUser2));
        gameRoomEntranceRepository.save(new GameRoomEntrance(testGameRoom, testUser3));
    }

    @EventListener(ContextClosedEvent.class)
    public void clearTestData() {
        userRepository.deleteAll();
        gameRoomRepository.deleteAll();
        gameRoomEntranceRepository.deleteAll();
    }
}
