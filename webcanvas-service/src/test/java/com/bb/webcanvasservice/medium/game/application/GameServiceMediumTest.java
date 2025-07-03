package com.bb.webcanvasservice.medium.game.application;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.dictionary.domain.model.Language;
import com.bb.webcanvasservice.dictionary.domain.model.PartOfSpeech;
import com.bb.webcanvasservice.dictionary.domain.model.Word;
import com.bb.webcanvasservice.dictionary.domain.repository.WordRepository;
import com.bb.webcanvasservice.game.application.command.JoinGameRoomCommand;
import com.bb.webcanvasservice.game.application.command.StartGameCommand;
import com.bb.webcanvasservice.game.application.command.UpdateReadyCommand;
import com.bb.webcanvasservice.game.application.config.GameProperties;
import com.bb.webcanvasservice.game.application.dto.*;
import com.bb.webcanvasservice.game.application.registry.GameSessionLoadRegistry;
import com.bb.webcanvasservice.game.application.registry.GameTurnTimerRegistry;
import com.bb.webcanvasservice.game.domain.model.session.GameSessionState;
import com.bb.webcanvasservice.game.domain.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.application.service.GameService;
import com.bb.webcanvasservice.game.application.service.GameTurnTimerService;
import com.bb.webcanvasservice.game.domain.model.room.*;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Tag("medium")
@DisplayName("[medium] [game] [service] 게임 애플리케이션 서비스 통합 test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class GameServiceMediumTest {

    @Autowired
    GameProperties gameProperties;

    @Autowired
    GameRoomRepository gameRoomRepository;

    @Autowired
    GameSessionLoadRegistry gameSessionLoadRegistry;

    @Autowired
    GameTurnTimerRegistry gameTurnTimerRegistry;

    @Autowired
    GameService gameService;

    @Autowired
    GameTurnTimerService gameTurnTimerService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    GameServiceTester gameServiceTester;


    @BeforeAll
    public void setUpOtherDomainData() {

        // user
        for (int i = 0; i < 100; i++) {
            userRepository.save(User.create(FingerprintGenerator.generate()));
        }

        // word
        List<Word> words = new ArrayList<>();
        Long wordIndex = 0L;
        words.add(Word.create(Language.KOREAN, "명사1", ++wordIndex, PartOfSpeech.NOUN));
        words.add(Word.create(Language.KOREAN, "명사2", ++wordIndex, PartOfSpeech.NOUN));
        words.add(Word.create(Language.KOREAN, "명사3", ++wordIndex, PartOfSpeech.NOUN));
        words.add(Word.create(Language.KOREAN, "명사4", ++wordIndex, PartOfSpeech.NOUN));

        words.add(Word.create(Language.KOREAN, "형용사1", ++wordIndex, PartOfSpeech.ADJECTIVE));
        words.add(Word.create(Language.KOREAN, "형용사2", ++wordIndex, PartOfSpeech.ADJECTIVE));
        words.add(Word.create(Language.KOREAN, "형용사3", ++wordIndex, PartOfSpeech.ADJECTIVE));
        words.add(Word.create(Language.KOREAN, "형용사4", ++wordIndex, PartOfSpeech.ADJECTIVE));
        wordRepository.saveInBatch(words);
    }


    @Test
    @DisplayName("게임 방 생성 및 입장 - 성공 테스트")
    void testCreateGameRoomAndEnter() throws Exception {
        // given
        Long userId = 1L;

        // when
        GameRoomJoinDto resultDto = gameService.createGameRoomAndEnter(userId);

        // then
        Assertions.assertThat(resultDto.gameRoomId()).isNotNull();
        Assertions.assertThat(resultDto.gameRoomParticipantId()).isNotNull();
    }

    @Test
    @DisplayName("입장 가능한 게임 방 조회 - 성공 테스트")
    void testFindJoinableGameRooms() throws Exception {
        // given
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long userId3 = 3L;
        Long userId4 = 4L;

        Long userId5 = 5L;

        GameRoomJoinDto gameRoomJoinDto1 = gameService.createGameRoomAndEnter(userId1);
        GameRoomJoinDto gameRoomJoinDto2 = gameService.createGameRoomAndEnter(userId2);
        GameRoomJoinDto gameRoomJoinDto3 = gameService.createGameRoomAndEnter(userId3);
        GameRoomJoinDto gameRoomJoinDto4 = gameService.createGameRoomAndEnter(userId4);


        // when
        GameRoomListDto joinableGameRooms = gameService.findJoinableGameRooms(userId5);

        // then
        Assertions.assertThat(joinableGameRooms.roomList().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("입장 가능한 게임 방 조회 - 게임 방 정원이 꽉찬 게임 방은 조회되지 않는다.")
    void testFindJoinableGameRooms_1() throws Exception {
        // given
        Long userIdSeq = 0L;

        GameRoomJoinDto gameRoomJoinDto1 = gameService.createGameRoomAndEnter(++userIdSeq);
        GameRoomJoinDto gameRoomJoinDto2 = gameService.createGameRoomAndEnter(++userIdSeq);
        GameRoomJoinDto gameRoomJoinDto3 = gameService.createGameRoomAndEnter(++userIdSeq);
        GameRoomJoinDto gameRoomJoinDto4 = gameService.createGameRoomAndEnter(++userIdSeq);

        // when
        for (int i = 1; i < gameProperties.gameRoomCapacity(); i++) {
            gameService.joinGameRoom(new JoinGameRoomCommand(gameRoomJoinDto1.gameRoomId(), ++userIdSeq));
        }
        GameRoomListDto joinableGameRooms = gameService.findJoinableGameRooms(++userIdSeq);

        // then
        Assertions.assertThat(joinableGameRooms.roomList().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("입장 가능한 게임 방 조회 - 게임방 상태가 WAITING인 게임 방만 조회된다.")
    void testFindJoinableGameRooms_2() throws Exception {
        // given
        Long userIdSeq = 0L;
        Long gameRoom1HostId = ++userIdSeq;
        GameRoomJoinDto gameRoomJoinDto1 = gameService.createGameRoomAndEnter(gameRoom1HostId);
        GameRoomJoinDto gameRoomJoinDto2 = gameService.createGameRoomAndEnter(++userIdSeq);
        GameRoomJoinDto gameRoomJoinDto3 = gameService.createGameRoomAndEnter(++userIdSeq);
        GameRoomJoinDto gameRoomJoinDto4 = gameService.createGameRoomAndEnter(++userIdSeq);

        // when
        Long gameRoom1GuestId = ++userIdSeq;
        GameRoomJoinDto gameRoomJoinDto = gameService.joinGameRoom(new JoinGameRoomCommand(gameRoomJoinDto1.gameRoomId(), gameRoom1GuestId));
        gameService.updateReady(new UpdateReadyCommand(gameRoomJoinDto.gameRoomParticipantId(), gameRoom1GuestId, true));
        gameService.loadGameSession(new StartGameCommand(gameRoomJoinDto1.gameRoomId(), 2, 20, gameRoom1HostId));

        GameRoomListDto joinableGameRooms = gameService.findJoinableGameRooms(++userIdSeq);
        // then

        Assertions.assertThat(joinableGameRooms.roomList().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("현재 입장한 게임 방 및 입장 정보 조회 - 성공 테스트")
    void testFindJoinedGameRoomInfo() throws Exception {
        // given
        Long userId = 1L;

        GameRoomJoinDto gameRoomAndEnter = gameService.createGameRoomAndEnter(userId);

        // when
        GameRoomJoinDetailInfoDto joinedGameRoomInfo = gameService.findJoinedGameRoomInfo(userId);

        JoinedUserInfoDto playerJoinInfo = new JoinedUserInfoDto(
                userId,
                "#ff3c00",
                "깔끔한 플레이어",
                GameRoomParticipantRole.HOST,
                true
        );
        GameRoomJoinDetailInfoDto resultDto = new GameRoomJoinDetailInfoDto(
                gameRoomAndEnter.gameRoomId(),
                gameRoomAndEnter.gameRoomParticipantId(),
                List.of(playerJoinInfo),
                GameRoomState.WAITING,
                playerJoinInfo
        );

        // then
        Assertions.assertThat(joinedGameRoomInfo)
                .usingRecursiveComparison()
                .ignoringFields("joinedUsers.nickname")
                .isEqualTo(resultDto);
    }

    @Test
    @DisplayName("레디 상태 변경 - 성공 테스트")
    void testUpdateReady() throws Exception {
        // given
        Long hostUserId = 1L;
        Long guestUserId = 2L;
        GameRoomJoinDto gameRoomAndEnter = gameService.createGameRoomAndEnter(hostUserId);

        GameRoomJoinDto gameRoomJoinDto = gameService.joinGameRoom(new JoinGameRoomCommand(gameRoomAndEnter.gameRoomId(), guestUserId));

        // when
        gameService.updateReady(new UpdateReadyCommand(gameRoomJoinDto.gameRoomParticipantId(), guestUserId, true));

        // then
        GameRoom gameRoom = gameRoomRepository.findGameRoomById(gameRoomJoinDto.gameRoomId()).orElseThrow(RuntimeException::new);
        GameRoomParticipant participant = gameRoom.findParticipant(gameRoomJoinDto.gameRoomParticipantId());

        Assertions.assertThat(participant.isReady()).isTrue();
    }

    @Test
    @DisplayName("호스트의 게임 시작 요청 - 게임 세션을 로드한다.")
    void testStartGame() throws Exception {
        // given
        Long hostUserId = 1L;
        Long guestUserId = 2L;
        GameRoomJoinDto gameRoomAndEnter = gameService.createGameRoomAndEnter(hostUserId);
        GameRoomJoinDto gameRoomJoinDto = gameService.joinGameRoom(new JoinGameRoomCommand(gameRoomAndEnter.gameRoomId(), guestUserId));

        gameService.updateReady(new UpdateReadyCommand(gameRoomJoinDto.gameRoomParticipantId(), guestUserId, true));

        // when
        Long gameSessionId = gameService.loadGameSession(new StartGameCommand(gameRoomJoinDto.gameRoomId(), 2, 20, hostUserId));
        GameRoom gameRoom = gameRoomRepository.findGameRoomById(gameRoomJoinDto.gameRoomId()).orElseThrow(RuntimeException::new);

        // then
        Assertions.assertThat(gameRoom.getCurrentGameSession()).isNotNull();
        Assertions.assertThat(gameRoom.getCurrentGameSession().getState()).isEqualTo(GameSessionState.LOADING);
        gameRoom.getParticipants().forEach(participant -> {
            Assertions.assertThat(participant.isLoading()).isTrue();
            Assertions.assertThat(participant.isReady()).isEqualTo(participant.isHost());
        });
    }

    @Test
    @DisplayName("게임 방 ID로 현재 게임 세션 조회 - 성공테스트")
    void 게임_방_ID로_현재_게임_세션을_조회한다() throws Exception {
        // given
        // given
        Long hostUserId = 1L;
        Long guestUserId = 2L;
        GameRoomJoinDto gameRoomAndEnter = gameService.createGameRoomAndEnter(hostUserId);
        GameRoomJoinDto gameRoomJoinDto = gameService.joinGameRoom(new JoinGameRoomCommand(gameRoomAndEnter.gameRoomId(), guestUserId));

        gameService.updateReady(new UpdateReadyCommand(gameRoomJoinDto.gameRoomParticipantId(), guestUserId, true));

        Long gameSessionId = gameService.loadGameSession(new StartGameCommand(gameRoomJoinDto.gameRoomId(), 2, 20, hostUserId));
        GameRoom gameRoom = gameRoomRepository.findGameRoomById(gameRoomJoinDto.gameRoomId()).orElseThrow(RuntimeException::new);

        // when
        GameSessionDto findCurrentGameSession = gameService.findCurrentGameSession(gameRoom.getId());

        var gameSession = gameRoom.getCurrentGameSession();

        var expectedResult = new GameSessionDto(
                gameSession.getId(),
                gameSession.getState(),
                gameSession.getTimePerTurn(),
                gameSession.getCompletedGameTurnCount(),
                gameSession.getTurnCount()
        );
        // then
        Assertions.assertThat(expectedResult).usingRecursiveComparison().isEqualTo(findCurrentGameSession);
    }

    @Test
    @DisplayName("게임 세션 시작 - 모든 클라이언트가 구독 성공 시 로드되어 있는 게임세션을 시작한다.")
    void 모든_클라이언트가_구독_성공시_로드되어_있는_게임세션을_시작한다() throws Exception {
        // given
        Long hostUserId = 99L;
        Long guestUserId = 100L;

        var testData = gameServiceTester.prepareGameSessionStartTestData(hostUserId, guestUserId);

        GameRoom gameRoom = gameRoomRepository.findGameRoomById(testData.gameRoomId()).orElseThrow(RuntimeException::new);

        AtomicBoolean hostResult = new AtomicBoolean(false);
        AtomicBoolean guestResult = new AtomicBoolean(false);

        // when
        CountDownLatch hostThreadLatch = new CountDownLatch(1);
        CountDownLatch guestThreadLatch = new CountDownLatch(1);

        // 비동기 실행
        Thread hostSubscriptionThread = new Thread(() -> {
            try {
                System.out.println("hostSubscriptionThrea");
                hostResult.set(gameService.successSubscription(testData.gameSessionId(), hostUserId));
            } finally {
                hostThreadLatch.countDown(); // 완료 알림
            }
        });
        Thread guestSubscriptionThread = new Thread(() -> {
            try {
                System.out.println("guestSubscriptionThread");
                guestResult.set(gameService.successSubscription(testData.gameSessionId(), guestUserId));
            } finally {
                guestThreadLatch.countDown(); // 완료 알림
            }
        });

        hostSubscriptionThread.start();
        guestSubscriptionThread.start();

        // 최대 2초 기다림
        boolean hostCompleted = hostThreadLatch.await(2, java.util.concurrent.TimeUnit.SECONDS);
        boolean guestCompleted = guestThreadLatch.await(2, java.util.concurrent.TimeUnit.SECONDS);


        // then
        Assertions.assertThat(hostCompleted && guestCompleted).isTrue();
        Assertions.assertThat(gameSessionLoadRegistry.isClear(testData.gameSessionId())).isTrue();

        // 순서에 상관없이 둘 중 한 군데에서는 모두 로드되었음을 체크 가능
        Assertions.assertThat(hostResult.get() || guestResult.get()).isTrue();

    }

}

