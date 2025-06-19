package com.bb.webcanvasservice.small.game.domain.model.gameroom;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.game.domain.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipant;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameSession;
import com.bb.webcanvasservice.user.domain.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Tag("small")
@DisplayName("[small] [game] 게임 방 도메인 모델 테스트")
class GameRoomTest {

    final int joinCodeLength = 6;
    final int roomCapacity = 8;

    @Test
    @DisplayName("게임 세션 로드 - 게임 방에서 세션을 로드한다. / 게임 시작 API  처리")
    void testLoadGameSession_success_1() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant gameRoomParticipant1 = GameRoomParticipant.create(gameRoom.getId(), user1.getId(), "테승트중인");
        GameRoomParticipant gameRoomParticipant2 = GameRoomParticipant.create(gameRoom.getId(), user2.getId(), "테승트중인");

        gameRoom.letIn(gameRoomParticipant1);
        gameRoom.letIn(gameRoomParticipant2);

        gameRoomParticipant2.changeReady(true);

        gameRoom.loadGameSession(150);

        // when
        GameSession gameSession = gameRoom.getCurrentGameSession();

        // then
        Assertions.assertThat(gameSession.getTimePerTurn()).isEqualTo(150);
        Assertions.assertThat(gameSession.isLoading()).isTrue();
    }

    @Test
    @DisplayName("게임 세션 로드 - 이미 진행중인 세션이 있다면 실패")
    void testLoadGameSession_failed_1() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant gameRoomParticipant1 = GameRoomParticipant.create(gameRoom.getId(), user1.getId(), "테스트중인");
        GameRoomParticipant gameRoomParticipant2 = GameRoomParticipant.create(gameRoom.getId(), user2.getId(), "테스트중인");

        gameRoom.letIn(gameRoomParticipant1);
        gameRoom.letIn(gameRoomParticipant2);

        gameRoomParticipant2.changeReady(true);

        gameRoom.loadGameSession(150);

        // when
        Assertions.assertThatThrownBy(() -> gameRoom.loadGameSession(200))
                .isInstanceOf(IllegalGameRoomStateException.class);

        // then
    }

    @Test
    @DisplayName("게임 세션 로드 - 모든 유저가 레디 하지 않으면 실패")
    void testLoadGameSession_failed_2() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant gameRoomParticipant1 = GameRoomParticipant.create(gameRoom.getId(), user1.getId(), "테스트중인");
        GameRoomParticipant gameRoomParticipant2 = GameRoomParticipant.create(gameRoom.getId(), user2.getId(), "테스트중인");

        gameRoom.letIn(gameRoomParticipant1);
        gameRoom.letIn(gameRoomParticipant2);

        // when

        Assertions.assertThatThrownBy(() -> gameRoom.loadGameSession(200))
                .isInstanceOf(IllegalGameRoomStateException.class);

        // then
    }

    @Test
    @DisplayName("게임 세션 로드 - 게임 진행 최소 인원보다 사람 수가 적으면 실패")
    void testLoadGameSession_failed_3() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant gameRoomParticipant1 = GameRoomParticipant.create(gameRoom.getId(), user1.getId(), "테스트중인");

        gameRoom.letIn(gameRoomParticipant1);

        // when
        Assertions.assertThatThrownBy(() -> gameRoom.loadGameSession(200))
                .isInstanceOf(IllegalGameRoomStateException.class);

        // then
    }

    @Test
    @DisplayName("게엠 방 입장 테스트 - 방의 상태가 Waiting 상태가 아니면 실패")
    void testCheckCanJoinFailed_1() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());
        User user3 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant user1Participant = GameRoomParticipant.create(gameRoom.getId(), user1.getId(), "테스트중인");
        GameRoomParticipant user2Participant = GameRoomParticipant.create(gameRoom.getId(), user2.getId(), "테스트중인");
        GameRoomParticipant user3Participant = GameRoomParticipant.create(gameRoom.getId(), user3.getId(), "테스트중인");

        gameRoom.letIn(user1Participant);
        gameRoom.letIn(user2Participant);

        gameRoom.close();

        // when
        Assertions.assertThatThrownBy(() -> gameRoom.letIn(user3Participant))
                .isInstanceOf(IllegalGameRoomStateException.class);
        // then
    }

    @Test
    @DisplayName("게엠 방 입장 테스트 - 방의 상태가 Waiting 상태이더라도 입장 정원이 가득 차면 실패")
    void testCheckCanJoinFailed_2() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());
        User user3 = User.create(FingerprintGenerator.generate());
        User user4 = User.create(FingerprintGenerator.generate());
        User user5 = User.create(FingerprintGenerator.generate());
        User user6 = User.create(FingerprintGenerator.generate());
        User user7 = User.create(FingerprintGenerator.generate());
        User user8 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant user1Participant = GameRoomParticipant.create(gameRoom.getId(), user1.getId(), "테스트중인");
        GameRoomParticipant user2Participant = GameRoomParticipant.create(gameRoom.getId(), user2.getId(), "테스트중인");
        GameRoomParticipant user3Participant = GameRoomParticipant.create(gameRoom.getId(), user3.getId(), "테스트중인");
        GameRoomParticipant user4Participant = GameRoomParticipant.create(gameRoom.getId(), user4.getId(), "테스트중인");
        GameRoomParticipant user5Participant = GameRoomParticipant.create(gameRoom.getId(), user5.getId(), "테스트중인");
        GameRoomParticipant user6Participant = GameRoomParticipant.create(gameRoom.getId(), user6.getId(), "테스트중인");
        GameRoomParticipant user7Participant = GameRoomParticipant.create(gameRoom.getId(), user7.getId(), "테스트중인");
        GameRoomParticipant user8Participant = GameRoomParticipant.create(gameRoom.getId(), user8.getId(), "테스트중인");

        GameRoomParticipant user9Participant = GameRoomParticipant.create(gameRoom.getId(), user2.getId(), "테스트중인");

        gameRoom.letIn(user1Participant);
        gameRoom.letIn(user2Participant);
        gameRoom.letIn(user3Participant);
        gameRoom.letIn(user4Participant);
        gameRoom.letIn(user5Participant);
        gameRoom.letIn(user6Participant);
        gameRoom.letIn(user7Participant);
        gameRoom.letIn(user8Participant);

        // when
        Assertions.assertThatThrownBy(() -> gameRoom.letIn(user9Participant))
                .isInstanceOf(IllegalGameRoomStateException.class);
        // then
    }

    @Test
    @DisplayName("게임 방 퇴장 테스트 - 게임 방에서 퇴장한다.")
    void testExitGameRoom_success_1() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant gameRoomParticipant1 = GameRoomParticipant.create(gameRoom.getId(), user1.getId(), "테스트중인");
        GameRoomParticipant gameRoomParticipant2 = GameRoomParticipant.create(gameRoom.getId(), user2.getId(), "테스트중인");

        gameRoom.letIn(gameRoomParticipant1);
        gameRoom.letIn(gameRoomParticipant2);

        // when
        gameRoom.sendOut(gameRoomParticipant2);

        // then
        List<GameRoomParticipant> currentParticipants = gameRoom.getCurrentParticipants();

        // 방에 남은 participant 수
        Assertions.assertThat(currentParticipants.size()).isEqualTo(1);
        // 방에서 나간 participant 상태
        Assertions.assertThat(gameRoomParticipant2.getState()).isEqualTo(GameRoomParticipantState.EXITED);

        // 본인을 제외하고 퇴장 이벤트 발행
        AtomicInteger publishedEventCount = new AtomicInteger(0);
        gameRoom.processEventQueue(event -> publishedEventCount.incrementAndGet());
        Assertions.assertThat(publishedEventCount.get()).isEqualTo(1);
    }
}