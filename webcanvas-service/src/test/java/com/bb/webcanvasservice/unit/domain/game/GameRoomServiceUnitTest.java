package com.bb.webcanvasservice.unit.domain.game;

import com.bb.webcanvasservice.common.JoinCodeGenerator;
import com.bb.webcanvasservice.domain.game.GameProperties;
import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.GameRoomService;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceInfoResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.AlreadyEnteredRoomException;
import com.bb.webcanvasservice.domain.game.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.bb.webcanvasservice.common.code.ErrorCode.GAME_ROOM_HAS_ILLEGAL_STATUS;
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

    /**
     * mock
     */
    private GameProperties gameProperties = new GameProperties(8, 10, 10);
    private GameRoomService gameRoomService;

    @BeforeEach
    void setUp() {
        this.gameRoomService = new GameRoomService(userService, gameRoomRepository, gameRoomEntranceRepository, gameProperties);
    }

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
        String joinCode = JoinCodeGenerator.generate(10);
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
        GameRoomEntranceResponse gameRoomEntranceResponse = gameRoomService.enterGameRoom(testGameRoomId, testUserId);

        // then
        Assertions.assertThat(gameRoomEntranceResponse.gameRoomEntranceId()).isEqualTo(testGameRoomEntranceId);
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
                .thenReturn(Optional.of(new GameRoom(GameRoomState.PLAYING, JoinCodeGenerator.generate(10))));

        long gameRoomId = random.nextLong();
        long userId = random.nextLong();

        // when
        Assertions.assertThatThrownBy(() -> gameRoomService.enterGameRoom(gameRoomId, userId))
                .isInstanceOf(IllegalGameRoomStateException.class)
                .hasMessage(GAME_ROOM_HAS_ILLEGAL_STATUS.getDefaultMessage());

        // then
    }

    @Test
    @DisplayName("GameRoom 입장 - 유저가 입장하려는 방의 현재 입장 정원이 모두 찬 경우 실패")
    public void enterGameRoomFailedWhenGameRoomLimitationIsOver() {
        // given
        when(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(any(Long.class)))
                .thenReturn(Boolean.FALSE);
        GameRoom testGameRoom = new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(10));
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
    @DisplayName("입장 가능한 방 목록 조회 - 현재 입장한 방이 있다면 목록 조회 실패")
    void findEnterableGameRoom() {
        // given
        when(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(any(Long.class)))
                .thenReturn(Boolean.TRUE);
        Long userId = random.nextLong();

        // when
        Assertions.assertThatThrownBy(() -> gameRoomService.findEnterableGameRooms(userId))
                .isInstanceOf(AlreadyEnteredRoomException.class);
        // then

    }

    @Test
    @DisplayName("현재 입장한 게임 방과 입장 정보를 리턴한다.")
    void findEnteredGameRoomInfo() throws Exception {
        // given
        var testGameRoom = new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(10));
        var testUser0 = new User(UUID.randomUUID().toString());
        var testUser1 = new User(UUID.randomUUID().toString());
        var testUser2 = new User(UUID.randomUUID().toString());
        var testGameRoomEntrance0 = new GameRoomEntrance(testGameRoom, testUser0);
        var testGameRoomEntrance1 = new GameRoomEntrance(testGameRoom, testUser1);
        var testGameRoomEntrance2 = new GameRoomEntrance(testGameRoom, testUser2);

        setId(testGameRoom, random.nextLong());
        setId(testUser0, random.nextLong());
        setId(testUser1, random.nextLong());
        setId(testUser2, random.nextLong());
        setId(testGameRoomEntrance0, random.nextLong());
        setId(testGameRoomEntrance1, random.nextLong());
        setId(testGameRoomEntrance2, random.nextLong());

        when(gameRoomEntranceRepository.findGameRoomEntranceByUserId(any(Long.class)))
                .thenReturn(Optional.of(testGameRoomEntrance0));

        when(gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomId(any(Long.class)))
                .thenReturn(List.of(testGameRoomEntrance0, testGameRoomEntrance1, testGameRoomEntrance2));

        // when

        GameRoomEntranceInfoResponse enteredGameRoomInfo = gameRoomService.findEnteredGameRoomInfo(testUser0.getId());

        // then
        Assertions.assertThat(enteredGameRoomInfo.gameRoomEntranceId()).isEqualTo(testGameRoomEntrance0.getId());
        Assertions.assertThat(enteredGameRoomInfo.gameRoomId()).isEqualTo(testGameRoom.getId());
        Assertions.assertThat(enteredGameRoomInfo.otherUsers()).hasSize(2);
    }

    @Test
    @DisplayName("Join Code로 게임 방 입장 - Join Code로 게임 방에 입장 후 입장한 방의 ID와 입장 ID를 리턴한다.")
    void enterGameRoom() throws Exception {
        // given
        var testRoom = new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(6));
        var testUser = new User(UUID.randomUUID().toString());
        var testGameRoomEntrance = new GameRoomEntrance(testRoom, testUser);

        setId(testUser, random.nextLong());
        setId(testRoom, random.nextLong());
        setId(testGameRoomEntrance, random.nextLong());

        /**
         * JoinCode 처리 mock
         */
        when(gameRoomRepository.findRoomWithJoinCodeForEnter(testRoom.getJoinCode()))
                .thenReturn(Optional.of(testRoom));

        when(gameRoomEntranceRepository.save(any())).thenReturn(testGameRoomEntrance);

        when(userService.findUserByUserId(testUser.getId())).thenReturn(testUser);

        /**
         * 입장 mock
         */
        when(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(any(Long.class))).thenReturn(Boolean.FALSE);
        when(gameRoomRepository.findById(testRoom.getId())).thenReturn(Optional.of(testRoom));

        // when
        GameRoomEntranceResponse gameRoomEntranceResponse = gameRoomService.enterGameRoomWithJoinCode(testRoom.getJoinCode(), testUser.getId());

        // then
        Assertions.assertThat(gameRoomEntranceResponse.gameRoomId()).isEqualTo(testRoom.getId());
        Assertions.assertThat(gameRoomEntranceResponse.gameRoomEntranceId()).isEqualTo(testGameRoomEntrance.getId());
    }



    private void setId(Object entity, Long id) throws NoSuchFieldException, IllegalAccessException {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }
}