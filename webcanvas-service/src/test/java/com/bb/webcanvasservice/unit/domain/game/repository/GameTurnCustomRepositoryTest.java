package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.common.config.JpaConfig;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameRoomEntranceJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameSessionJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameTurnJpaEntity;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceRole;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomEntranceJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameSessionJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameTurnJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.user.entity.UserJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.user.repository.UserJpaRepository;
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
    GameTurnJpaRepository gameTurnRepository;

    @Autowired
    GameRoomJpaRepository gameRoomRepository;

    @Autowired
    GameRoomEntranceJpaRepository gameRoomEntranceRepository;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    GameSessionJpaRepository gameSessionRepository;

    @Test
    @DisplayName("현재 턴 찾기 - 없을 경우 Optional null 리턴")
    void testFindLastTurn() throws Exception {
        // given
        UserJpaEntity user1 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));
        UserJpaEntity user2 = userJpaRepository.save(new UserJpaEntity(FingerprintGenerator.generate()));

        GameRoomJpaEntity gameRoom = gameRoomRepository.save(new GameRoomJpaEntity(JoinCodeGenerator.generate(6)));

        GameRoomEntranceJpaEntity gameRoomEntrance1 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user1, "닉네임1", GameRoomEntranceRole.HOST));
        GameRoomEntranceJpaEntity gameRoomEntrance2 = gameRoomEntranceRepository.save(new GameRoomEntranceJpaEntity(gameRoom, user1, "닉네임1", GameRoomEntranceRole.GUEST));

        gameRoomEntrance2.changeReady(true);

        GameSessionJpaEntity gameSession = gameSessionRepository.save(new GameSessionJpaEntity(gameRoom, 2, 90));
        GameTurnJpaEntity gameTurn = gameTurnRepository.save(new GameTurnJpaEntity(gameSession, user1, "정답"));

        // when
        Assertions.assertThat(gameTurnRepository.findLatestTurn(gameSession.getId())).isPresent();
        Assertions.assertThat(gameTurnRepository.findLatestTurn(gameSession.getId()).get()).isEqualTo(gameTurn);

        // then
    }

}