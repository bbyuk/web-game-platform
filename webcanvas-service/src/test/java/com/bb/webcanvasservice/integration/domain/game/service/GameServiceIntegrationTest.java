package com.bb.webcanvasservice.integration.domain.game.service;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.game.service.GameRoomFacade;
import com.bb.webcanvasservice.domain.game.service.GameService;
import com.bb.webcanvasservice.domain.user.entity.User;
import com.bb.webcanvasservice.domain.user.enums.UserStateCode;
import com.bb.webcanvasservice.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;

@Transactional
@SpringBootTest
@DisplayName("[integration] [service] 게임 서비스 통합테스트")
class GameServiceIntegrationTest {


    @Autowired
    GameService gameService;

    @Autowired
    GameRoomFacade gameRoomFacade;

    @Autowired
    UserRepository userRepository;

    @MockitoBean
    DictionaryService dictionaryService;

    @Test
    @DisplayName("게임 방 퇴장 - 퇴장시 유저 상태가 IN_LOBBY로 변경된다.")
    public void exitFromGameRoom() throws Exception {
        // given
        BDDMockito.given(dictionaryService.drawRandomWordValue(any(), any()))
                .willReturn("테스트 명사");

        User user1 = userRepository.save(new User(FingerprintGenerator.generate()));
        User user2 = userRepository.save(new User(FingerprintGenerator.generate()));

        GameRoomEntranceResponse user1Entrance = gameRoomFacade.createGameRoomAndEnter(user1.getId());
        GameRoomEntranceResponse user2Entrance = gameRoomFacade.enterGameRoom(user1Entrance.gameRoomId(), user2.getId(), GameRoomRole.GUEST);

        // when
        gameRoomFacade.exitFromRoom(user2Entrance.gameRoomEntranceId(), user2.getId());

        // then
        Assertions.assertThat(user2.getState()).isEqualTo(UserStateCode.IN_LOBBY);
    }
}