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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class GameRoomEntranceRepositoryTest {

    @Autowired
    private GameRoomEntranceRepository gameRoomEntranceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRoomRepository gameRoomRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User(UUID.randomUUID().toString()));
    }

    @Test
    @DisplayName("게임 방 입장 여부 조회 - 게임 방에 입장해있지 않으면 false 리턴")
    void existsGameRoomEntranceByUserIdFalse() {
        // given

        // when
        boolean exists = gameRoomEntranceRepository.existsGameRoomEntranceByUserId(testUser.getId());

        // then
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("게임 방 입장 여부 조회 - 게임 방에 입장해있다면 true 리턴")
    void existsGameRoomEntranceByUserIdTrue() {
        // given
        enterTestRoom(testUser);

        // when
        boolean exists = gameRoomEntranceRepository.existsGameRoomEntranceByUserId(testUser.getId());

        // then
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("입장한 방 찾기 - 입장한 방이 없다면 Optional.empty() 리턴")
    void findByUserIdFailed() {
        // given

        // when
        Assertions.assertThat(gameRoomEntranceRepository.findByUserId(testUser.getId()))
                .isEmpty();


        // then
    }

    @Test
    @DisplayName("입장한 방 찾기 - 입장한 방이 있다면 리턴")
    void findByUserIdSuccess() {
        // given
        enterTestRoom(testUser);

        // when
        Assertions.assertThat(gameRoomEntranceRepository.findByUserId(testUser.getId())).isPresent();

        // then
    }

    @Test
    @DisplayName("게임 방 ID로 해당 게임 방에 입장한 정보 조회")
    void findGameRoomEntrancesByGameRoomId() {
        // given
        User otherUser1 = userRepository.save(new User(UUID.randomUUID().toString()));
        User otherUser2 = userRepository.save(new User(UUID.randomUUID().toString()));
        User otherUser3 = userRepository.save(new User(UUID.randomUUID().toString()));
        User otherUser4 = userRepository.save(new User(UUID.randomUUID().toString()));

        enterTestRoom(otherUser1, testUser, otherUser2, otherUser3, otherUser4);

        // when
        List<GameRoomEntrance> gameRoomEntrances = gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomId(testUser.getId());

        // then
        Assertions.assertThat(gameRoomEntrances.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("유저 ID로 현재 입장한 게임 방의 입장 정보 조회")
    void findGameRoomEntrancesByUserId() {
        // given
        User otherUser1 = userRepository.save(new User(UUID.randomUUID().toString()));
        User otherUser2 = userRepository.save(new User(UUID.randomUUID().toString()));
        User otherUser3 = userRepository.save(new User(UUID.randomUUID().toString()));
        User otherUser4 = userRepository.save(new User(UUID.randomUUID().toString()));

        enterTestRoom(otherUser1, testUser, otherUser2, otherUser3, otherUser4);
        // when
        Optional<GameRoomEntrance> gameRoomEntrance = gameRoomEntranceRepository.findGameRoomEntranceByUserId(testUser.getId());

        // then
        Assertions.assertThat(gameRoomEntrance).isPresent();
        Assertions.assertThat(gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomId(gameRoomEntrance.get().getGameRoom().getId()))
                .hasSize(5);
    }

    private void enterTestRoom(User... enteredUsers) {
        GameRoom gameRoom = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(6)));
        Arrays.stream(enteredUsers).forEach(enteredUser -> gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, enteredUser)));
    }


}