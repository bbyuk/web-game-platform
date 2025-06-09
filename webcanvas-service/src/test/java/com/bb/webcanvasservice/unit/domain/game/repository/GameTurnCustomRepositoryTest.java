package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.config.JpaConfig;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import com.bb.webcanvasservice.domain.game.repository.GameTurnRepository;
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

@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("[unit] [persistence] 게임 턴 Repository 단위테스트")
class GameTurnCustomRepositoryTest {

    @Autowired
    GameTurnRepository gameTurnRepository;

    @Autowired
    GameRoomRepository gameRoomRepository;

    @Autowired
    GameRoomEntranceRepository gameRoomEntranceRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GameSessionRepository gameSessionRepository;

    @Test
    @DisplayName("현재 턴 찾기 - 없을 경우 Optional null 리턴")
    void testFindLastTurn() throws Exception {
        // given
        User user1 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userRepository.save(new User(FingerprintGenerator.generate()));

        GameRoom gameRoom = gameRoomRepository.save(new GameRoom(JoinCodeGenerator.generate(6)));

        GameRoomEntrance gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user1, "닉네임1", GameRoomRole.HOST));
        GameRoomEntrance gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntrance(gameRoom, user1, "닉네임1", GameRoomRole.GUEST));

        gameRoomEntrance2.changeReady(true);

        GameSession gameSession = gameSessionRepository.save(new GameSession(gameRoom, 2, 90));
        GameTurn gameTurn = gameTurnRepository.save(new GameTurn(gameSession, user1, "정답"));

        // when
        Assertions.assertThat(gameTurnRepository.findLatestTurn(gameSession.getId())).isPresent();
        Assertions.assertThat(gameTurnRepository.findLatestTurn(gameSession.getId()).get()).isEqualTo(gameTurn);

        // then
    }

}