package com.bb.webcanvasservice.small.game.domain.model.gameroom;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.game.domain.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipant;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantRole;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameSession;
import com.bb.webcanvasservice.user.domain.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("small")
@DisplayName("[small] [game] 게임 방 도메인 모델 테스트")
class GameRoomTest {

    final int joinCodeLength = 6;
    final int roomCapacity = 8;

    @Test
    @DisplayName("게임 방을 로드한다.")
    void testGetCurrentGameSession() throws Exception {
        // given
        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);
        gameRoom.loadGameSession(150);

        // when
        GameSession gameSession = gameRoom.getCurrentGameSession();

        // then
        Assertions.assertThat(gameSession.getTimePerTurn()).isEqualTo(150);
        Assertions.assertThat(gameSession.isLoading()).isTrue();
    }

    @Test
    @DisplayName("게엠 방이 입장 가능한 상태인지 체크 - 방의 상태가 Waiting 상태가 아니면 실패")
    void testCheckCanJoinFailed_1() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant user1Participant = GameRoomParticipant.create(gameRoom.getId(), user1.getId(), "테스트중인");
        GameRoomParticipant user2Participant = GameRoomParticipant.create(gameRoom.getId(), user2.getId(), "테스트중인");

        gameRoom.letIn(user1Participant);
        gameRoom.letIn(user2Participant);

        gameRoom.close();

        // when
        Assertions.assertThatThrownBy(() -> gameRoom.checkCanJoin())
                .isInstanceOf(IllegalGameRoomStateException.class);
        // then
    }
}