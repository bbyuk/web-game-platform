package com.bb.webcanvasservice.medium.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.config.JpaConfig;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipant;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.GameRoomRepositoryImpl;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserRepositoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import({JpaConfig.class, GameRoomRepositoryImpl.class, UserRepositoryImpl.class})
@DisplayName("[medium] [game] [persistence] Game Repository 영속성 테스트")
public class GameRepositoryTest {


    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private UserRepository userRepository;

    int joinCodeLength = 6;

    int roomCapacity = 5;


    @Test
    @DisplayName("게임방 ID로 게임 방 찾기 - 입장자, 게임 세션, 게임 턴 없는 경우")
    void 게임방_ID로_게임_방_찾기_1() throws Exception {
        // given
        GameRoom savedGameRoom = gameRoomRepository.save(GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity));

        // when
        gameRoomRepository.findGameRoomById(savedGameRoom.getId())
                .ifPresent(findGameRoom -> {
                    Assertions.assertThat(findGameRoom).usingRecursiveComparison().isEqualTo(savedGameRoom);
                });

        // then

    }

    @Test
    @DisplayName("게임 방 ID로 게임 방 찾기 - 입장자만 있는 경우")
    void 게임방_ID로_게임_방_찾기_2() throws Exception {
        // given
        User hostUser = userRepository.save(User.create(FingerprintGenerator.generate()));
        User guestUser = userRepository.save(User.create(FingerprintGenerator.generate()));

        GameRoom savedGameRoom = gameRoomRepository.save(GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity));

        GameRoomParticipant host = GameRoomParticipant.create(hostUser.getId(), "호스트");
        GameRoomParticipant guest = GameRoomParticipant.create(guestUser.getId(), "게스트");

        savedGameRoom.letIn(host);
        savedGameRoom.letIn(guest);

        gameRoomRepository.save(savedGameRoom);

        // when

        // then
    }

}
