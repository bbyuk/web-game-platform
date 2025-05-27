package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.config.JpaConfig;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import com.bb.webcanvasservice.domain.user.entity.User;
import com.bb.webcanvasservice.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static com.bb.webcanvasservice.domain.game.enums.GameRoomRole.*;

@Transactional
@DataJpaTest
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("[unit] [persistence] 게임 플레이 관련 GameSession Repository 단위테스트")
class GameSessionRepositoryTest {

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private GameRoomEntranceRepository gameRoomEntranceRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("현재 라운드 조회 - 현재 라운드를 조회한다.")
    void testFindCurrentRound() throws Exception {
        // given
        User user1 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user3 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user4 = userRepository.save(new User(FingerprintGenerator.generate()));

        GameRoom gameRoom = gameRoomRepository.save(new GameRoom(JoinCodeGenerator.generate(6)));

        GameRoomEntrance gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user1, "유저1", HOST));
        GameRoomEntrance gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user2, "유저2", GUEST));
        GameRoomEntrance gameRoomEntrance3 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user3, "유저3", GUEST));
        GameRoomEntrance gameRoomEntrance4 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user4, "유저4", GUEST));

        GameSession gameSession = gameSessionRepository.save(new GameSession(gameRoom, 6, 90));
        // when
        int currentRound = gameSessionRepository.findCurrentRound(gameSession.getId());

        // then
        Assertions.assertThat(currentRound).isEqualTo(1);
    }

}