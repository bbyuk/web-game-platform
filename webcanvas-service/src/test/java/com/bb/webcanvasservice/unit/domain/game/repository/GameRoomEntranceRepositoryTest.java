package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.common.config.JpaConfig;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameRoomEntranceJpaEntity;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceRole;
import com.bb.webcanvasservice.domain.game.model.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.GameRoomEntranceNotFoundException;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomEntranceJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.user.entity.UserJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.user.repository.UserJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("[unit] [persistence] 게임 방 입장 Repository 단위테스트")
class GameRoomEntranceRepositoryTest {

    @Autowired
    private GameRoomEntranceJpaRepository gameRoomEntranceRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private GameRoomJpaRepository gameRoomRepository;

    private UserJpaEntity testUser;

    @BeforeEach
    void setup() {
        testUser = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
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
    @DisplayName("게임 방 입장 여부 조회 - 게임 방 입장 기록 중 WAITING 상태가 없다면 false return")
    void existsGameRoomEntranceByUserIdFalseWhenNoActive() {
        // given
        enterTestRoom(testUser);
        GameRoomEntranceJpaEntity gameRoomEntrance = gameRoomEntranceRepository.findGameRoomEntranceByUserIdAndGameRoomStates(testUser.getId(), List.of(GameRoomEntranceState.WAITING, GameRoomEntranceState.PLAYING)).orElseThrow(() -> new GameRoomEntranceNotFoundException("왜 여기서,,?"));
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
        UserJpaEntity otherUser1JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        UserJpaEntity otherUser2JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        UserJpaEntity otherUser3JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        UserJpaEntity otherUser4JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));

        enterTestRoom(otherUser1JpaEntity, testUser, otherUser2JpaEntity, otherUser3JpaEntity, otherUser4JpaEntity);
        // when
        Optional<GameRoomEntranceJpaEntity> gameRoomEntrance = gameRoomEntranceRepository.findGameRoomEntranceByUserIdAndGameRoomStates(testUser.getId(), List.of(GameRoomEntranceState.WAITING, GameRoomEntranceState.PLAYING));

        // then
        Assertions.assertThat(gameRoomEntrance).isPresent();
        List<GameRoomEntranceJpaEntity> entrances = gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdAndState(gameRoomEntrance.get().getGameRoom().getId(), GameRoomEntranceState.WAITING);
        Assertions.assertThat(entrances)
                .hasSize(5);

        // 250430 입장한 순서대로 enteredUsers sorting 추가
        Assertions.assertThat(entrances.get(0).getUser()).isEqualTo(otherUser1JpaEntity);
        Assertions.assertThat(entrances.get(1).getUser()).isEqualTo(testUser);
        Assertions.assertThat(entrances.get(2).getUser()).isEqualTo(otherUser2JpaEntity);
        Assertions.assertThat(entrances.get(3).getUser()).isEqualTo(otherUser3JpaEntity);
        Assertions.assertThat(entrances.get(4).getUser()).isEqualTo(otherUser4JpaEntity);
    }

    @Test
    @DisplayName("GameRoom에 입장되어 있는지 여부를 조회한다.")
    void findGameRoomEntrancesByGameRoomId() throws Exception {
        // given
        /**
         * testUser1은 입장, testUser2는 입장하지 않음
         */
        GameRoomJpaEntity testGameRoom = gameRoomRepository.save(new GameRoomJpaEntity(GameRoomState.WAITING, JoinCodeGenerator.generate(6)));

        UserJpaEntity testUser1JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        UserJpaEntity testUser2JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));

        gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(testGameRoom, testUser1JpaEntity, "테스트 여우", GameRoomEntranceRole.HOST));

        // when
        boolean isTestUser1EnteredTestGameRoom = gameRoomEntranceRepository.existsActiveEntrance(testGameRoom.getId(), testUser1JpaEntity.getId());
        boolean isTestUser2EnteredTestGameRoom = gameRoomEntranceRepository.existsActiveEntrance(testGameRoom.getId(), testUser2JpaEntity.getId());

        // then
        Assertions.assertThat(isTestUser1EnteredTestGameRoom).isTrue();
        Assertions.assertThat(isTestUser2EnteredTestGameRoom).isFalse();
    }

    private void enterTestRoom(UserJpaEntity... enteredUserJpaEntities) {
        GameRoomJpaEntity gameRoom = gameRoomRepository.save(new GameRoomJpaEntity(GameRoomState.WAITING, JoinCodeGenerator.generate(6)));
        Arrays.stream(enteredUserJpaEntities).forEach(enteredUser -> gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, enteredUser, "테스트 수달", GameRoomEntranceRole.GUEST)));
    }

    public void exit(GameRoomEntranceJpaEntity entrance) {
        entrance.exit();
    }

}