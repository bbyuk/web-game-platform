package com.bb.webcanvasservice.unit.domain.game.service;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.domain.game.GameProperties;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceInfoResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomListResponse;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.AlreadyEnteredRoomException;
import com.bb.webcanvasservice.domain.game.exception.GameRoomHostCanNotChangeReadyException;
import com.bb.webcanvasservice.domain.game.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.game.service.GameRoomCrossDomainService;
import com.bb.webcanvasservice.domain.game.service.GameRoomFacade;
import com.bb.webcanvasservice.domain.game.service.GameRoomInnerService;
import com.bb.webcanvasservice.domain.game.service.LobbyService;
import com.bb.webcanvasservice.domain.user.entity.User;
import com.bb.webcanvasservice.domain.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.bb.webcanvasservice.common.code.ErrorCode.GAME_ROOM_HAS_ILLEGAL_STATUS;
import static com.bb.webcanvasservice.domain.game.enums.GameRoomRole.GUEST;
import static com.bb.webcanvasservice.domain.game.enums.GameRoomRole.HOST;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[unit] [service] 게임 방 서비스 단위테스트")
class GameRoomFacadeUnitTest {

    @Mock
    private GameRoomRepository gameRoomRepository;
    @Mock
    private GameRoomEntranceRepository gameRoomEntranceRepository;
    @Mock
    private UserService userService;
    @Mock
    private DictionaryService dictionaryService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    /**
     * mock
     */
    private GameProperties gameProperties = new GameProperties(8, 10, 10, List.of("#ff3c00", "#0042ff", "#1e9000", "#f2cb00", "#8400a8", "#00c8c8", "#ff68ff", "#969696"), List.of(
            "수달",
            "늑대",
            "고양이",
            "부엉이",
            "사막여우",
            "호랑이",
            "너구리",
            "다람쥐"
    ));
    private GameRoomFacade gameRoomFacade;

    @BeforeEach
    void setup() {
        this.gameRoomFacade = new GameRoomFacade(
                new LobbyService(gameProperties, userService, dictionaryService, gameRoomRepository, gameRoomEntranceRepository, eventPublisher),
                new GameRoomInnerService(gameRoomRepository, gameRoomEntranceRepository, gameProperties, eventPublisher),
                new GameRoomCrossDomainService(gameRoomEntranceRepository, gameRoomRepository)
        );
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


        when(userService.findUser(any(Long.class)))
                .thenReturn(testUser);


        // 테스트 게임 방
        String joinCode = JoinCodeGenerator.generate(10);
        GameRoom testGameRoom = new GameRoom(GameRoomState.WAITING, joinCode);
        long testGameRoomId = random.nextLong();
        setId(testGameRoom, testGameRoomId);

        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, alreadyEnteredUser1, "테스트 늑대", GUEST));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, alreadyEnteredUser2, "테스트 수달", GUEST));

        when(gameRoomRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(testGameRoom));

        // 테스트 게임 방 입장
        GameRoomEntrance testGameRoomEntrance = new GameRoomEntrance(testGameRoom, testUser, "테스트 여우", GUEST);
        long testGameRoomEntranceId = random.nextLong();
        setId(testGameRoomEntrance, testGameRoomEntranceId);

        when(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(any(Long.class)))
                .thenReturn(Boolean.FALSE);
        when(gameRoomEntranceRepository.save(any(GameRoomEntrance.class)))
                .thenReturn(testGameRoomEntrance);


        // when
        GameRoomEntranceResponse gameRoomEntranceResponse = gameRoomFacade.enterGameRoom(testGameRoomId, testUserId, GUEST);

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
        Assertions.assertThatThrownBy(() -> gameRoomFacade.enterGameRoom(gameRoomId, userId, GUEST))
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
        Assertions.assertThatThrownBy(() -> gameRoomFacade.enterGameRoom(gameRoomId, userId, GUEST))
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
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString()), "테스트 여우", HOST));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString()), "테스트 수달", GUEST));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString()), "테스트 늑대", GUEST));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString()), "테스트 고양이", GUEST));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString()), "테스트 부엉이", GUEST));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString()), "테스트 다람쥐", GUEST));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString()), "테스트 호랑이", GUEST));
        testGameRoom.addEntrance(new GameRoomEntrance(testGameRoom, new User(UUID.randomUUID().toString()), "테스트 너구리", GUEST));

        when(gameRoomRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(testGameRoom));
        when(gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdAndState(anyLong(), any()))
                .thenReturn(testGameRoom.getEntrances());

        long gameRoomId = random.nextLong();
        long userId = random.nextLong();

        // when


        Assertions.assertThatThrownBy(() -> gameRoomFacade.enterGameRoom(gameRoomId, userId, GUEST))
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
        Assertions.assertThatThrownBy(() -> gameRoomFacade.findEnterableGameRooms(userId))
                .isInstanceOf(AlreadyEnteredRoomException.class);
        // then

    }

    @Test
    @DisplayName("입장 가능한 방 목록 조회 - 입장 가능한 방의 현재 인원, 최대 정원을 함꼐 리턴한다")
    void findEnterableGameRoomsWithCounts() throws Exception {
        // given
        String joinCode = JoinCodeGenerator.generate(6);
        GameRoom gameRoom = new GameRoom(GameRoomState.WAITING, joinCode);
        setId(gameRoom, 1L);

        User user1 = new User(UUID.randomUUID().toString());
        User user2 = new User(UUID.randomUUID().toString());
        User user3 = new User(UUID.randomUUID().toString());
        User user4 = new User(UUID.randomUUID().toString());

        setId(user1, 1L);
        setId(user2, 2L);
        setId(user3, 3L);
        setId(user4, 4L);


        GameRoomEntrance gameRoomEntrance1 = new GameRoomEntrance(gameRoom, user1, "유저1", GUEST);
        GameRoomEntrance gameRoomEntrance2 = new GameRoomEntrance(gameRoom, user2, "유저2", GUEST);
        GameRoomEntrance gameRoomEntrance3 = new GameRoomEntrance(gameRoom, user3, "유저3", GUEST);
        setId(gameRoomEntrance1, 1L);
        setId(gameRoomEntrance2, 2L);
        setId(gameRoomEntrance3, 3L);

        gameRoom.addEntrance(gameRoomEntrance1);
        gameRoom.addEntrance(gameRoomEntrance2);
        gameRoom.addEntrance(gameRoomEntrance3);

        // 이러면 gameRoom에 둘
        // state만 변경하고 다시 add
        gameRoomEntrance3.exit();
        gameRoom.addEntrance(gameRoomEntrance3);

        when(gameRoomRepository.findGameRoomsByCapacityAndStateWithEntranceState(anyInt(), any(), any()))
                .thenReturn(List.of(gameRoom));
        when(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(any()))
                .thenReturn(false);
//        when(gameProperties.gameRoomCapacity())
//                .thenReturn(8);

        // when
        GameRoomListResponse enterableGameRooms = gameRoomFacade.findEnterableGameRooms(4L);

        // then
        Assertions.assertThat(enterableGameRooms).usingRecursiveComparison().isEqualTo(new GameRoomListResponse(
                List.of(new GameRoomListResponse.GameRoomSummary(1L, 8, 2, joinCode))
        ));
    }

    @Test
    @DisplayName("현재 입장한 게임 방과 입장 정보를 리턴한다.")
    void findEnteredGameRoomInfo() throws Exception {
        // given
        var testGameRoom = new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(10));
        var testUser0 = new User(UUID.randomUUID().toString());
        var testUser1 = new User(UUID.randomUUID().toString());
        var testUser2 = new User(UUID.randomUUID().toString());
        var testGameRoomEntrance0 = new GameRoomEntrance(testGameRoom, testUser0, "테스트 부엉이", HOST);
        var testGameRoomEntrance1 = new GameRoomEntrance(testGameRoom, testUser1, "테스트 고양이", GUEST);
        var testGameRoomEntrance2 = new GameRoomEntrance(testGameRoom, testUser2, "테스트 호랑이", GUEST);

        setId(testGameRoom, random.nextLong());
        setId(testUser0, random.nextLong());
        setId(testUser1, random.nextLong());
        setId(testUser2, random.nextLong());
        setId(testGameRoomEntrance0, random.nextLong());
        setId(testGameRoomEntrance1, random.nextLong());
        setId(testGameRoomEntrance2, random.nextLong());

        when(gameRoomEntranceRepository.findGameRoomEntranceByUserId(any(Long.class)))
                .thenReturn(Optional.of(testGameRoomEntrance0));

        when(gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdAndState(any(Long.class), any()))
                .thenReturn(List.of(testGameRoomEntrance0, testGameRoomEntrance1, testGameRoomEntrance2));


        // when

        GameRoomEntranceInfoResponse enteredGameRoomInfo0 = gameRoomFacade.findEnteredGameRoomInfo(testUser0.getId());


        // then
        Assertions.assertThat(enteredGameRoomInfo0.gameRoomEntranceId()).isEqualTo(testGameRoomEntrance0.getId());
        Assertions.assertThat(enteredGameRoomInfo0.gameRoomId()).isEqualTo(testGameRoom.getId());
        Assertions.assertThat(enteredGameRoomInfo0.enteredUsers()).hasSize(3);


        // 250430 - 유저 Summary 데이터에 노출 컬러 필드 추가
        Assertions.assertThat(enteredGameRoomInfo0.enteredUsers().get(0).color()).isEqualTo("#ff3c00");
        Assertions.assertThat(enteredGameRoomInfo0.enteredUsers().get(1).color()).isEqualTo("#0042ff");
        Assertions.assertThat(enteredGameRoomInfo0.enteredUsers().get(2).color()).isEqualTo("#1e9000");

        // 250520 - 게임 방 상태 필드 추가
        Assertions.assertThat(enteredGameRoomInfo0.gameRoomState()).isEqualTo(GameRoomState.WAITING);

        // 250521 - 게임 방 ROLE 필드 추가
        Assertions.assertThat(enteredGameRoomInfo0.requesterUserSummary().role()).isEqualTo(HOST);

        // 250523 - 게임 방 ready 필드 추가 / HOST는 무조건 레디 상태
        Assertions.assertThat(enteredGameRoomInfo0.requesterUserSummary().ready()).isTrue();
    }

    @Test
    @DisplayName("Join Code로 게임 방 입장 - Join Code로 게임 방에 입장 후 입장한 방의 ID와 입장 ID를 리턴한다.")
    void enterGameRoom() throws Exception {
        // given
        var testRoom = new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(6));
        var testUser = new User(UUID.randomUUID().toString());
        var testGameRoomEntrance = new GameRoomEntrance(testRoom, testUser, "테스트 여우", GUEST);

        setId(testUser, random.nextLong());
        setId(testRoom, random.nextLong());
        setId(testGameRoomEntrance, random.nextLong());

        /**
         * JoinCode 처리 mock
         */
        when(gameRoomRepository.findRoomWithJoinCodeForEnter(testRoom.getJoinCode()))
                .thenReturn(Optional.of(testRoom));

        when(gameRoomEntranceRepository.save(any())).thenReturn(testGameRoomEntrance);

        when(userService.findUser(testUser.getId())).thenReturn(testUser);

        /**
         * 입장 mock
         */
        when(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(any(Long.class))).thenReturn(Boolean.FALSE);
        when(gameRoomRepository.findById(testRoom.getId())).thenReturn(Optional.of(testRoom));

        // when
        GameRoomEntranceResponse gameRoomEntranceResponse = gameRoomFacade.enterGameRoomWithJoinCode(testRoom.getJoinCode(), testUser.getId());

        // then
        Assertions.assertThat(gameRoomEntranceResponse.gameRoomId()).isEqualTo(testRoom.getId());
        Assertions.assertThat(gameRoomEntranceResponse.gameRoomEntranceId()).isEqualTo(testGameRoomEntrance.getId());
    }

    @Test
    @DisplayName("레디 상태 변경 - 호스트는 게임 레디 상태를 임의로 변경할 수 없다.")
    void hostCannotChangeReady() throws Exception {
        // given
        User user = new User(FingerprintGenerator.generate());
        setId(user, 1L);
        GameRoom gameRoom = new GameRoom(GameRoomState.WAITING, JoinCodeGenerator.generate(6));

        GameRoomEntrance gameRoomEntrance = new GameRoomEntrance(gameRoom, user, "닉네임1", HOST);

        when(gameRoomEntranceRepository.findById(anyLong())).thenReturn(Optional.of(gameRoomEntrance));

        // when

        Assertions.assertThatThrownBy(() -> gameRoomFacade.updateReady(1L, 1L, true))
                .isInstanceOf(GameRoomHostCanNotChangeReadyException.class);

        // then
    }

    private void setId(Object entity, Long id) throws NoSuchFieldException, IllegalAccessException {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }
}