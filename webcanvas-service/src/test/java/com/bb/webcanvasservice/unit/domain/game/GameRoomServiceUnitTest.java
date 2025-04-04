package com.bb.webcanvasservice.unit.domain.game;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.GameRoomService;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.AlreadyEnteredRoomException;
import com.bb.webcanvasservice.domain.game.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameRoomServiceUnitTest {

    @Mock
    private GameRoomRepository gameRoomRepository;
    @Mock
    private GameRoomEntranceRepository gameRoomEntranceRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private GameRoomService gameRoomService;

    private final Random random = new Random();

    @Test
    @DisplayName("GameRoom 입장 - 유저가 현재 입장한 방이 없고, 방의 상태가 입장 가능한 상태이면 성공적으로 입장")
    public void enterGameRoomSuccess() throws Exception {
        // given
        String testUserToken = UUID.randomUUID().toString();

        User alreadyEnteredUser1 = new User(UUID.randomUUID().toString());
        User alreadyEnteredUser2 = new User(UUID.randomUUID().toString());


        // 테스트 유저
        User testUser = new User(testUserToken);
        long testUserId = random.nextLong();
        setId(testUser, testUserId);

        when(userService.findUserByUserId(any(Long.class)))
                .thenReturn(testUser);

        // 테스트 게임 방
        String joinCode = RandomCodeGenerator.generate(10);
        GameRoom testGameRoom = new GameRoom(GameRoomState.WAITING, joinCode);
        long testGameRoomId = random.nextLong();
        setId(testGameRoom, testGameRoomId);

        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, alreadyEnteredUser1));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, alreadyEnteredUser2));

        when(gameRoomRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(testGameRoom));

        // 테스트 게임 방 입장
        GameRoomEntrance testGameRoomEntrance = new GameRoomEntrance(testGameRoom, testUser);
        long testGameRoomEntranceId = random.nextLong();
        setId(testGameRoomEntrance, testGameRoomEntranceId);

        when(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(any(Long.class)))
                .thenReturn(Boolean.FALSE);
        when(gameRoomEntranceRepository.save(any(GameRoomEntrance.class)))
                .thenReturn(testGameRoomEntrance);


        // when
        Long gameRoomEnterId = gameRoomService.enterGameRoom(testGameRoomId, testUserId);

        // then
        Assertions.assertThat(gameRoomEnterId).isEqualTo(testGameRoomEntranceId);
    }


    @Test
    @DisplayName("GameRoom 입장 - 유저가 현재 입장한 방이 있다면 실패")
    public void enterGameRoomFailedWhenAlreadyUserEnteredRoom() {
        // given
        when(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(any(Long.class)))
                .thenReturn(Boolean.TRUE);

        Long gameRoomId = random.nextLong();
        Long userId = random.nextLong();

        // when
        Assertions.assertThatThrownBy(() -> gameRoomService.enterGameRoom(gameRoomId, userId))
                .isInstanceOf(AlreadyEnteredRoomException.class);

        // then
    }

    @Test
    @DisplayName("GameRoom 입장 - 유저가 입장하려는 방의 상태가 WAITING이 아니면 실패")
    public void enterGameRoomFailedWhenGameRoomStateIsNotWaiting() {
        // given
        when(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(any(Long.class)))
                .thenReturn(Boolean.FALSE);
        when(gameRoomRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(new GameRoom(GameRoomState.PLAYING, RandomCodeGenerator.generate(10))));

        long gameRoomId = random.nextLong();
        long userId = random.nextLong();

        // when
        Assertions.assertThatThrownBy(() -> gameRoomService.enterGameRoom(gameRoomId, userId))
                .isInstanceOf(IllegalGameRoomStateException.class)
                .hasMessage("방이 현재 입장할 수 없는 상태입니다.");

        // then
    }

    @Test
    @DisplayName("GameRoom 입장 - 유저가 입장하려는 방의 현재 입장 정원이 모두 찬 경우 실패")
    public void enterGameRoomFailedWhenGameRoomLimitationIsOver() {
        // given
        when(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(any(Long.class)))
                .thenReturn(Boolean.FALSE);
        GameRoom testGameRoom = new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(10));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString())));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString())));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString())));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString())));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString())));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString())));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString())));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString())));

        when(gameRoomRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(testGameRoom));

        long gameRoomId = random.nextLong();
        long userId = random.nextLong();

        // when


        Assertions.assertThatThrownBy(() -> gameRoomService.enterGameRoom(gameRoomId, userId))
                .isInstanceOf(IllegalGameRoomStateException.class)
                .hasMessage("방의 정원이 모두 찼습니다.");

        // then
    }


    @Test
    @DisplayName("입장 가능한 방 목록 조회")
    void findEnterableGameRoom() {
        // given

        // when

        // then

    }


    private void setId(Object entity, Long id) throws NoSuchFieldException, IllegalAccessException {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }
}