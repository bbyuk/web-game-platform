package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GameRoomRepositoryTest {

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private GameRoomEntranceRepository gameRoomEntranceRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("유저 입장 기록이 있는 방 중 현재 방 상태가 CLOSED가 아닌 방 조회")
    void findNotClosedGameRoomByUserId() {
        // given
        User testUser = userRepository.save(new User(UUID.randomUUID().toString()));
        GameRoom savedGameRoom = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(6)));
        gameRoomEntranceRepository.save(new GameRoomEntrance(savedGameRoom, testUser));


        User testUser1 = userRepository.save(new User(UUID.randomUUID().toString()));
        gameRoomEntranceRepository.save(new GameRoomEntrance(savedGameRoom, testUser1));

        User testUser2 = userRepository.save(new User(UUID.randomUUID().toString()));
        gameRoomEntranceRepository.save(new GameRoomEntrance(savedGameRoom, testUser2));


        // when
        GameRoom findGameRoom = gameRoomRepository.findNotClosedGameRoomByUserId(testUser.getId())
                .get();
        GameRoom findTestUser1GameRoom = gameRoomRepository.findNotClosedGameRoomByUserId(testUser1.getId()).get();

        // then
        Assertions.assertThat(savedGameRoom.getId()).isEqualTo(findGameRoom.getId());
        Assertions.assertThat(findTestUser1GameRoom.getId()).isEqualTo(savedGameRoom.getId());
    }

    @Test
    @DisplayName("GameRoom 상태로 게임 방 조회")
    void findByState() throws Exception {
        // given
        gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(6)));
        gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(6)));
        gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(6)));
        gameRoomRepository.save(new GameRoom(GameRoomState.PLAYING, RandomCodeGenerator.generate(6)));
        gameRoomRepository.save(new GameRoom(GameRoomState.PLAYING, RandomCodeGenerator.generate(6)));
        gameRoomRepository.save(new GameRoom(GameRoomState.CLOSED, RandomCodeGenerator.generate(6)));

        // when
        List<GameRoom> waitingRooms = gameRoomRepository.findByState(GameRoomState.WAITING);
        List<GameRoom> playingRooms = gameRoomRepository.findByState(GameRoomState.PLAYING);
        List<GameRoom> closedRooms = gameRoomRepository.findByState(GameRoomState.CLOSED);

        // then
        Assertions.assertThat(waitingRooms.size()).isEqualTo(3);
        Assertions.assertThat(playingRooms.size()).isEqualTo(2);
        Assertions.assertThat(closedRooms.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("입장 가능한 GameRoom 목록 조회")
    void findEnterableGameRooms() {
        // given
        /**
         * 입장가능 room1 ~ room3
         */
        GameRoom room1 = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(6)));
        GameRoom room2 = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(6)));
        GameRoom room3 = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(6)));

        /**
         * PLAYING room4
         */
        GameRoom room4 = gameRoomRepository.save(new GameRoom(GameRoomState.PLAYING, RandomCodeGenerator.generate(6)));

        /**
         * WAITING - 입장인원 MAX
         */
        GameRoom room5 = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(6)));

        gameRoomEntranceRepository.saveAll(
                Stream.generate(() -> new GameRoomEntrance(room5, userRepository.save(new User(UUID.randomUUID().toString()))))
                        .limit(GameRoom.CAPACITY)
                        .collect(Collectors.toList())
        );


        // when
        List<GameRoom> enterableGameRooms = gameRoomRepository.findEnterableGameRooms(GameRoom.CAPACITY, GameRoomState.enterable());

        // then
        Assertions.assertThat(enterableGameRooms.size()).isEqualTo(3);
    }
}