package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.common.config.JpaConfig;
import com.bb.webcanvasservice.game.application.config.GameProperties;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.game.infrastructure.persistence.entity.GameRoomEntranceJpaEntity;
import com.bb.webcanvasservice.game.domain.model.GameRoomEntranceRole;
import com.bb.webcanvasservice.game.domain.model.GameRoomState;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.GameRoomEntranceJpaRepository;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.GameRoomJpaRepository;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserJpaRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Transactional
@DataJpaTest
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("[unit] [persistence] 게임 방 Repository 단위테스트")
class GameRoomRepositoryTest {

    @Autowired
    private GameRoomJpaRepository gameRoomRepository;

    @Autowired
    private GameRoomEntranceJpaRepository gameRoomEntranceRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager em;

    private GameProperties gameProperties = new GameProperties(8, 10, 10, new ArrayList<>(), List.of(
            "수달",
            "늑대",
            "고양이",
            "부엉이",
            "사막여우",
            "호랑이",
            "너구리",
            "다람쥐"
    ));


    @Test
    @DisplayName("유저 입장 기록이 있는 방 중 현재 방 상태가 CLOSED가 아닌 방 조회")
    void findNotClosedGameRoomByUserId() {
        // given
        UserJpaEntity testUser = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        GameRoomJpaEntity savedGameRoom = gameRoomRepository.save(new GameRoomJpaEntity(GameRoomState.WAITING, JoinCodeGenerator.generate(6)));
        gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(savedGameRoom, testUser, "테스트 부엉이", GameRoomEntranceRole.GUEST));


        UserJpaEntity testUser1JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(savedGameRoom, testUser1JpaEntity, "테스트 다람쥐", GameRoomEntranceRole.GUEST));

        UserJpaEntity testUser2JpaEntity = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(savedGameRoom, testUser2JpaEntity, "테스트 고양이", GameRoomEntranceRole.GUEST));


        // when
        GameRoomJpaEntity findGameRoom = gameRoomRepository.findNotClosedGameRoomByUserId(testUser.getId())
                .get();
        GameRoomJpaEntity findTestUser1GameRoom = gameRoomRepository.findNotClosedGameRoomByUserId(testUser1JpaEntity.getId()).get();

        // then
        Assertions.assertThat(savedGameRoom.getId()).isEqualTo(findGameRoom.getId());
        Assertions.assertThat(findTestUser1GameRoom.getId()).isEqualTo(savedGameRoom.getId());
    }

//    @Test
//    @DisplayName("입장 가능한 GameRoom 목록 조회")
//    void findEnterableGameRooms() {
//        // given
//        /**
//         * 입장가능 room1 ~ room3
//         */
//        GameRoom room1 = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(6)));
//        gameRoomEntranceRepository.save(new GameRoomEntrance(room1, userRepository.save(new User(UUID.randomUUID().toString())), "여우", GameRoomRole.GUEST));
//        GameRoom room2 = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(6)));
//        gameRoomEntranceRepository.save(new GameRoomEntrance(room2, userRepository.save(new User(UUID.randomUUID().toString())), "여우", GameRoomRole.GUEST));
//        GameRoom room3 = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(6)));
//        gameRoomEntranceRepository.save(new GameRoomEntrance(room3, userRepository.save(new User(UUID.randomUUID().toString())), "여우", GameRoomRole.GUEST));
//
//
//        /**
//         * PLAYING room4
//         */
//        GameRoom room4 = gameRoomRepository.save(new GameRoom(GameRoomState.PLAYING, JoinCodeGenerator.generate(6)));
//        gameRoomEntranceRepository.saveAll(
//                Stream.generate(() -> new GameRoomEntrance(room4, userRepository.save(new User(UUID.randomUUID().toString())), "테스트 호랑이", GameRoomRole.GUEST))
//                        .limit(gameProperties.gameRoomCapacity())
//                        .collect(Collectors.toList())
//        );
//
//        /**
//         * WAITING - 입장인원 MAX
//         */
//        GameRoom room5 = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(6)));
//
//        gameRoomEntranceRepository.saveAll(
//                Stream.generate(() -> new GameRoomEntrance(room5, userRepository.save(new User(UUID.randomUUID().toString())), "테스트 호랑이", GameRoomRole.GUEST))
//                        .limit(gameProperties.gameRoomCapacity())
//                        .collect(Collectors.toList())
//        );
//
//
//        // when
//        List<GameRoom> enterableGameRooms = gameRoomRepository.findGameRoomsByCapacityAndStateWithEntranceState(gameProperties.gameRoomCapacity(), GameRoomState.enterable(), GameRoomEntranceState.WAITING);
//
//
//        // then
//        Assertions.assertThat(enterableGameRooms.size()).isEqualTo(9);
//    }

    @Test
    @DisplayName("입장 가능한 GameRoom 목록 조회 - 중간에 퇴장한 유저가 있다면 entrances에 조회되지 않아야 한다.")
    void testFindGameRoomCanEnter() {
        // given
        /**
         * 입장가능 room1 ~ room3
         */
        GameRoomJpaEntity room1 = gameRoomRepository.save(new GameRoomJpaEntity(GameRoomState.WAITING, JoinCodeGenerator.generate(6)));
        gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(room1, userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString())), "여우", GameRoomEntranceRole.GUEST));
        UserJpaEntity user2 = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(room1, user2, "여우", GameRoomEntranceRole.GUEST));
        UserJpaEntity user3 = userJpaRepository.save(new UserJpaEntity(UUID.randomUUID().toString()));
        gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(room1, user3, "여우", GameRoomEntranceRole.GUEST));

        // when

        // then
    }
}