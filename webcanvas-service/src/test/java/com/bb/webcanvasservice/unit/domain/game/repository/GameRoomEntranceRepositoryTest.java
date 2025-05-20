package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.JoinCodeGenerator;
import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.GameRoomEntranceNotFoundException;
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
@DisplayName("[unit] [persistence] 게임 방 입장 Repository 단위테스트")
class GameRoomEntranceRepositoryTest {

    @Autowired
    private GameRoomEntranceRepository gameRoomEntranceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRoomRepository gameRoomRepository;

    private User testUser;

    @BeforeEach
    void setup() {
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
    @DisplayName("게임 방 입장 여부 조회 - 게임 방 입장 기록 중 ACTIVE 상태가 없다면 false return")
    void existsGameRoomEntranceByUserIdFalseWhenNoActive() {
        // given
        enterTestRoom(testUser);
        GameRoomEntrance gameRoomEntrance = gameRoomEntranceRepository.findGameRoomEntranceByUserId(testUser.getId()).orElseThrow(() -> new GameRoomEntranceNotFoundException("왜 여기서,,?"));
        exit(gameRoomEntrance);

        // when
        Assertions.assertThat(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(testUser.getId())).isFalse();


        // then
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
        List<GameRoomEntrance> entrances = gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomId(gameRoomEntrance.get().getGameRoom().getId());
        Assertions.assertThat(entrances)
                .hasSize(5);

        // 250430 입장한 순서대로 enteredUsers sorting 추가
        Assertions.assertThat(entrances.get(0).getUser()).isEqualTo(otherUser1);
        Assertions.assertThat(entrances.get(1).getUser()).isEqualTo(testUser);
        Assertions.assertThat(entrances.get(2).getUser()).isEqualTo(otherUser2);
        Assertions.assertThat(entrances.get(3).getUser()).isEqualTo(otherUser3);
        Assertions.assertThat(entrances.get(4).getUser()).isEqualTo(otherUser4);
    }

    @Test
    @DisplayName("GameRoom에 입장되어 있는지 여부를 조회한다.")
    void findGameRoomEntrancesByGameRoomId() throws Exception {
        // given
        /**
         * testUser1은 입장, testUser2는 입장하지 않음
         */
        GameRoom testGameRoom = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(6)));

        User testUser1 = userRepository.save(new User(UUID.randomUUID().toString()));
        User testUser2 = userRepository.save(new User(UUID.randomUUID().toString()));

        gameRoomEntranceRepository.save(new GameRoomEntrance(testGameRoom, testUser1, "테스트 여우", GameRoomRole.HOST));

        // when
        boolean isTestUser1EnteredTestGameRoom = gameRoomEntranceRepository.existsActiveEntrance(testGameRoom.getId(), testUser1.getId());
        boolean isTestUser2EnteredTestGameRoom = gameRoomEntranceRepository.existsActiveEntrance(testGameRoom.getId(), testUser2.getId());

        // then
        Assertions.assertThat(isTestUser1EnteredTestGameRoom).isTrue();
        Assertions.assertThat(isTestUser2EnteredTestGameRoom).isFalse();
    }

    private void enterTestRoom(User... enteredUsers) {
        GameRoom gameRoom = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(6)));
        Arrays.stream(enteredUsers).forEach(enteredUser -> gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, enteredUser, "테스트 수달", GameRoomRole.GUEST)));
    }

    public void exit(GameRoomEntrance entrance) {
        entrance.exit();
    }

}