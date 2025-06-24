package com.bb.webcanvasservice.medium.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.config.JpaConfig;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.infrastructure.persistence.repository.GameRoomRepositoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import({JpaConfig.class, GameRoomRepositoryImpl.class})
@DisplayName("[medium] [game] [persistence] Game Repository 영속성 테스트")
public class GameRepositoryTest {
    
    
    @Autowired
    private GameRoomRepository gameRoomRepository;

    int joinCodeLength = 6;

    int roomCapacity = 5;


    @Test
    @DisplayName("게임방 ID로 게임 방 찾기 - 성공 테스트")
    void 게임방_ID로_게임_방_찾기() throws Exception {
        // given
        GameRoom savedGameRoom = gameRoomRepository.save(GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity));

        // when
        gameRoomRepository.findGameRoomById(savedGameRoom.getId())
                .ifPresent(findGameRoom -> {
                    Assertions.assertThat(findGameRoom).usingRecursiveComparison().isEqualTo(savedGameRoom);
                });

        // then

    }

}
