package com.bb.webcanvasservice.small.game.domain.model.room;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.game.domain.event.UserReadyChanged;
import com.bb.webcanvasservice.game.domain.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.game.domain.model.room.GameRoom;
import com.bb.webcanvasservice.game.domain.model.room.GameRoomParticipant;
import com.bb.webcanvasservice.game.domain.model.room.GameRoomParticipantState;
import com.bb.webcanvasservice.user.domain.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Tag("small")
@DisplayName("[small] [game/room] [domain] 게임 방 도메인 모델 테스트")
class GameRoomTest {

    final int joinCodeLength = 6;
    final int roomCapacity = 8;

    @Test
    @DisplayName("게엠 방 입장 - 방의 상태가 Waiting 상태가 아니면 실패")
    void testCheckCanJoinFailed_1() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());
        User user3 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant user1Participant = GameRoomParticipant.create(user1.id(), "테스트중인");
        GameRoomParticipant user2Participant = GameRoomParticipant.create(user2.id(), "테스트중인");
        GameRoomParticipant user3Participant = GameRoomParticipant.create(user3.id(), "테스트중인");

        gameRoom.letIn(user1Participant);
        gameRoom.letIn(user2Participant);

        gameRoom.close();

        // when
        Assertions.assertThatThrownBy(() -> gameRoom.letIn(user3Participant))
                .isInstanceOf(IllegalGameRoomStateException.class);
        // then
    }

    @Test
    @DisplayName("게엠 방 입장 - 방의 상태가 Waiting 상태이더라도 입장 정원이 가득 차면 실패")
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

        GameRoomParticipant user1Participant = GameRoomParticipant.create(user1.id(), "테스트중인");
        GameRoomParticipant user2Participant = GameRoomParticipant.create(user2.id(), "테스트중인");
        GameRoomParticipant user3Participant = GameRoomParticipant.create(user3.id(), "테스트중인");
        GameRoomParticipant user4Participant = GameRoomParticipant.create(user4.id(), "테스트중인");
        GameRoomParticipant user5Participant = GameRoomParticipant.create(user5.id(), "테스트중인");
        GameRoomParticipant user6Participant = GameRoomParticipant.create(user6.id(), "테스트중인");
        GameRoomParticipant user7Participant = GameRoomParticipant.create(user7.id(), "테스트중인");
        GameRoomParticipant user8Participant = GameRoomParticipant.create(user8.id(), "테스트중인");

        GameRoomParticipant user9Participant = GameRoomParticipant.create(user2.id(), "테스트중인");

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
    @DisplayName("게임 방 퇴장 - 게임 방에서 퇴장한다.")
    void testExitGameRoom_success_1() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant gameRoomParticipant1 = GameRoomParticipant.create(user1.id(), "테스트중인");
        GameRoomParticipant gameRoomParticipant2 = GameRoomParticipant.create(user2.id(), "테스트중인");

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

    @Test
    @DisplayName("게임 방 퇴장 - 호스트가 퇴장할 때 방에 인원이 남아있다면 GUEST 중 가장 먼저 입장한 유저가 HOST가 된다.")
    void testExitGameRoom_success_2() throws Exception {
        // given
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());

        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);

        GameRoomParticipant gameRoomParticipant1 = GameRoomParticipant.create(user1.id(), "테스트중인");
        GameRoomParticipant gameRoomParticipant2 = GameRoomParticipant.create(user2.id(), "테스트중인");

        gameRoom.letIn(gameRoomParticipant1);
        gameRoom.letIn(gameRoomParticipant2);

        // when
        gameRoom.sendOut(gameRoomParticipant1);

        // then

        Assertions.assertThat(gameRoomParticipant2.isHost()).isTrue();
    }

    @Test
    @DisplayName("레디 상태 변경 - 게임 방 내 임의의 유저는 레디 상태를 변경할 수 있다.")
    void testGameRoomParticipantChangeReady_success1() throws Exception {
        // given
        // given
        var userIdField = User.class.getDeclaredField("id");
        userIdField.setAccessible(true);
        User user1 = User.create(FingerprintGenerator.generate());
        User user2 = User.create(FingerprintGenerator.generate());
        ReflectionUtils.setField(userIdField, user1, 1L);
        ReflectionUtils.setField(userIdField, user2, 2L);

        var gameRoomField = GameRoom.class.getDeclaredField("id");
        gameRoomField.setAccessible(true);
        GameRoom gameRoom = GameRoom.create(JoinCodeGenerator.generate(joinCodeLength), roomCapacity);
        ReflectionUtils.setField(gameRoomField, gameRoom, 1L);

        var participantIdField = GameRoomParticipant.class.getDeclaredField("id");
        participantIdField.setAccessible(true);

        GameRoomParticipant gameRoomParticipant1 = GameRoomParticipant.create(user1.id(), "테스트중인");
        GameRoomParticipant gameRoomParticipant2 = GameRoomParticipant.create(user2.id(), "테스트중인");
        ReflectionUtils.setField(participantIdField, gameRoomParticipant1, 1L);
        ReflectionUtils.setField(participantIdField, gameRoomParticipant2, 2L);

        gameRoom.letIn(gameRoomParticipant1);
        gameRoom.letIn(gameRoomParticipant2);

        // when
        gameRoom.changeParticipantReady(gameRoomParticipant2, true);
        List<Long> readyUserList = List.of(gameRoomParticipant2.getUserId());

        // then
        Assertions.assertThat(gameRoomParticipant2.isReady()).isTrue();
        gameRoom.processEventQueue(event -> {
            Assertions.assertThat(event).isInstanceOf(UserReadyChanged.class);
            UserReadyChanged userReadyChanged = (UserReadyChanged) event;
            Assertions.assertThat(readyUserList.contains(userReadyChanged.getUserId()));
        });
    }

}