package com.bb.webcanvasservice.game.application.service;

import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.game.application.command.*;
import com.bb.webcanvasservice.game.application.config.GameProperties;
import com.bb.webcanvasservice.game.application.dto.*;
import com.bb.webcanvasservice.game.application.registry.GameSessionLoadRegistry;
import com.bb.webcanvasservice.game.domain.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.game.domain.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.event.GameRoomJoinEvent;
import com.bb.webcanvasservice.game.domain.event.GameTurnProgressedEvent;
import com.bb.webcanvasservice.game.domain.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.GameRoomParticipantNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.JoinCodeNotGeneratedException;
import com.bb.webcanvasservice.game.domain.model.GamePlayHistory;
import com.bb.webcanvasservice.game.domain.model.gameroom.*;
import com.bb.webcanvasservice.game.domain.port.dictionary.GameDictionaryQueryPort;
import com.bb.webcanvasservice.game.domain.port.user.GameUserCommandPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {
    /**
     * 크로스 도메인 포트
     */
    private final GameDictionaryQueryPort dictionaryQueryPort;
    private final GameUserCommandPort userCommandPort;

    /**
     * 도메인 레포지토리
     */
    private final GameRoomRepository gameRoomRepository;
    private final GamePlayHistoryRepository gamePlayHistoryRepository;
    private final GameSessionLoadRegistry gameSessionLoadRegistry;

    /**
     * common layer
     */
    private final GameProperties gameProperties;
    private final ApplicationEventPublisher eventPublisher;


    /**
     * 게임 방을 새로 생성하고, 생성을 요청한 유저를 입장시킨다.
     *
     * @param userId
     * @return gameRoomParticipantId
     */
    @Transactional
    public GameRoomJoinDto createGameRoomAndEnter(Long userId) {
        /**
         * TODO userId 로 게임 방 생성 가능한지 validation 추가
         */
        GameRoom gameRoom = createGameRoom(gameProperties.joinCodeLength(), gameProperties.joinCodeMaxConflictCount());
        return joinGameRoom(new JoinGameRoomCommand(gameRoom.getId(), userId));
    }

    /**
     * 입장 가능한 게임 방을 조회해 리턴한다.
     * <p>
     * 이미 입장한 방이 있는 경우, AlreadyEnteredRoomException 을 throw한다.
     * <p>
     * TODO 메모리에서 WAITING GameRoomParticipant filtering 처리 -> batch size 및 페이징 처리 필요
     *
     * @param userId 유저 ID
     * @return GameRoomListResponse 게임 방 조회 응답 DTO
     */
    @Transactional
    public GameRoomListDto findJoinableGameRooms(Long userId) {
        /**
         * 유저 상태 validation
         */
        userCommandPort.validateUserCanJoin(userId);

        /**
         * 내 입장에서 입장 가능한 방 목록 조회
         *
         * 1. 게임 방이 정원이 차있으면 안됨.
         * 2. 게임 방이 WAITING 상태여야함.
         */
        return new GameRoomListDto(
                gameRoomRepository.findGameRoomsByCapacityAndGameRoomStateAndGameRoomParticipantState(
                                GameRoomState.WAITING,
                                GameRoomParticipantState.WAITING)
                        .stream()
                        .map(gameRoom ->
                                new GameRoomInfoDto(
                                        gameRoom.getId(),
                                        gameProperties.gameRoomCapacity(),
                                        gameRoom.getCurrentParticipants().size(),
                                        gameRoom.getJoinCode()
                                )
                        )
                        .collect(Collectors.toList())
        );
    }

    /**
     * 게임 방에 유저를 입장시킨다.
     * <p>
     * - 입장시키려는 유저가 현재 아무 방에도 접속하지 않은 상태여야 한다.
     * - 입장하려는 방의 상태가 WAITING이어야 한다.
     * - 입장하려는 방에 접속한 유저 세션의 수(participants)는 최대 8이다.
     *
     * @param command 게임 방 입장 커맨드
     * @return gameRoomParticipantId 게임 방 입장 ID
     */
    @Transactional
    public GameRoomJoinDto joinGameRoom(JoinGameRoomCommand command) {
        /**
         * 대상 게임 방에 입장할 수 있는지 체크한다.
         * GameRoomUserQueryPort를 통한 유저 상태 체크
         */
        userCommandPort.validateUserCanJoin(command.userId());

        GameRoom gameRoom = gameRoomRepository.findGameRoomById(command.gameRoomId()).orElseThrow(GameRoomNotFoundException::new);

        /**
         * dictionary 도메인 서비스로부터 랜덤 형용사 조회
         * 명사와 결합하여 랜덤 닉네임 할당
         *
         * HOST로 입장한다면 participant의 ready상태를 true로 초기 설정
         */
        String koreanAdjective = dictionaryQueryPort.drawRandomKoreanAdjective();

        GameRoomParticipant newGameRoomParticipant = GameRoomParticipant.create(
                command.userId()
                , koreanAdjective
        );
        gameRoom.letIn(newGameRoomParticipant);

        userCommandPort.moveUserToRoom(command.userId());

        /**
         * 게임 방 입장 이벤트 pub ->
         * 게임 방 broker에 입장 send 위임
         */
        eventPublisher.publishEvent(
                new GameRoomJoinEvent(
                        newGameRoomParticipant.getGameRoomId(),
                        newGameRoomParticipant.getUserId()
                )
        );

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);


        return new GameRoomJoinDto(
                savedGameRoom.getId(),
                savedGameRoom.getCurrentParticipantByUserId(command.userId()).getId()
        );
    }

    /**
     * Join Code로 게임 방에 입장한다.
     *
     * @param joinCode 입장 코드
     * @param userId   유저 ID
     * @return 입장 리턴 DTO
     */
    @Transactional
    public GameRoomJoinDto joinGameRoomWithJoinCode(String joinCode, Long userId) {
        GameRoom targetGameRoom = gameRoomRepository.findGameRoomByJoinCodeAndState(joinCode, GameRoomState.WAITING)
                .orElseThrow(() -> new GameRoomNotFoundException(String.format("입장 코드가 %s인 방을 찾지 못했습니다.", joinCode)));

        return joinGameRoom(
                new JoinGameRoomCommand(targetGameRoom.getId(), userId)
        );
    }


    /**
     * 현재 입장한 게임 방과 입장 정보를 리턴한다.
     * <p>
     * <p>
     * 250430 - 유저 Summary 데이터에 노출 컬러 필드 추가
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public GameRoomJoinDetailInfoDto findJoinedGameRoomInfo(Long userId) {
        GameRoom gameRoom = gameRoomRepository.findCurrentJoinedGameRoomByUserId(userId).orElseThrow(GameRoomNotFoundException::new);

        GameRoomParticipant requestedParticipant = gameRoom.findParticipantByUserId(userId);

        AtomicInteger index = new AtomicInteger(0);

        List<GameRoomParticipant> gameRoomParticipants = gameRoom.getCurrentParticipants();

        List<JoinedUserInfoDto> enteredUserSummaries = gameRoomParticipants
                .stream()
                .map(gameRoomParticipant ->
                        new JoinedUserInfoDto(
                                gameRoomParticipant.getUserId(),
                                gameProperties.gameRoomUserColors().get(index.getAndIncrement()),
                                gameRoomParticipant.getNickname(),
                                gameRoomParticipant.getRole(),
                                gameRoomParticipant.isReady()
                        )
                )
                .collect(Collectors.toList());


        return new GameRoomJoinDetailInfoDto(
                requestedParticipant.getGameRoomId(),
                requestedParticipant.getId(),
                enteredUserSummaries,
                gameRoom.getState(),
                enteredUserSummaries.stream().filter(enteredUserSummary
                        -> enteredUserSummary.userId().equals(userId)).findFirst().orElseThrow(GameRoomParticipantNotFoundException::new)
        );
    }

    /**
     * 게임 방에서 퇴장한다.
     * <p>
     * HOST 퇴장 시 입장한 지 가장 오래된 유저가 HOST로 변경
     *
     * @param command 게임 방 퇴장 커맨드
     */
    @Transactional
    public void exitFromRoom(ExitGameRoomCommand command) {
        GameRoom gameRoom = gameRoomRepository.findGameRoomByGameRoomParticipantId(command.gameRoomParticipantId()).orElseThrow(GameRoomNotFoundException::new);
        GameRoomParticipant targetParticipant = gameRoom.findParticipant(command.gameRoomParticipantId());

        /**
         * 비정상 요청인지 validate
         */
        targetParticipant.validate(command.userId());

        /**
         * 게임 방에서 대상 입장자를 내보낸다.
         */
        gameRoom.sendOut(targetParticipant);

        /**
         * 유저 상태도 변경
         */
        userCommandPort.moveUserToLobby(command.userId());

        gameRoomRepository.save(gameRoom);
        /**
         * 250519 게임방 퇴장시 event 발행
         * 250618 DDD에 맞춰 애그리거트 루트에 이벤트 큐를 두고 게임 방 퇴장 처리 중 발생한 이벤트 큐 publish
         */
        gameRoom.processEventQueue(eventPublisher::publishEvent);
    }

    /**
     * 게임 방에 입장한 유저의 레디 값을 변경한다.
     *
     * @param command 레디 변경 커맨드
     * @return 레디 여부
     */
    @Transactional
    public boolean updateReady(UpdateReadyCommand command) {
        GameRoom gameRoom = gameRoomRepository.findGameRoomByGameRoomParticipantId(command.gameRoomParticipantId()).orElseThrow(GameRoomParticipantNotFoundException::new);

        gameRoom.changeParticipantReady(
                gameRoom.findParticipant(command.gameRoomParticipantId()),
                command.ready()
        );

        gameRoomRepository.save(gameRoom);
        log.debug("게임 방 레디 변경 저장 = {}", command.gameRoomParticipantId());

        gameRoom.processEventQueue(eventPublisher::publishEvent);

        return command.ready();
    }


    /**
     * 게임 방 입장코드를 사용할 수 있는지 verify 하고 충돌 발생시 최대 충돌 가능 threshold까지 redraw하여 리턴한다.
     *
     * @param joinCode                 대상 입장코드
     * @param joinCodeMaxConflictCount 최대 충돌 가능 threshold
     * @return verified join code
     */
    @Transactional
    public String verifyJoinCode(String joinCode, int joinCodeMaxConflictCount) {
        String verifiedJoinCode = joinCode;
        int conflictCount = 0;

        while (gameRoomRepository.existsJoinCodeConflictOnActiveGameRoom(verifiedJoinCode)) {
            if (conflictCount == joinCodeMaxConflictCount) {
                throw new JoinCodeNotGeneratedException();
            }
            conflictCount++;
            verifiedJoinCode = JoinCodeGenerator.generate(joinCode.length());
        }
        return verifiedJoinCode;
    }

    /**
     * 게임 방을 생성한다.
     *
     * @param joinCodeLength           입장 코드 최대 길이
     * @param joinCodeMaxConflictCount
     * @return
     */
    @Transactional
    public GameRoom createGameRoom(int joinCodeLength, int joinCodeMaxConflictCount) {
        /**
         * join code 생성
         *
         * 1. 랜덤 코드 생성
         * 2. 랜덤 코드 충돌 확인
         *      2.1. 현재 방의 상태가 closed가 아닌 게임 방 중 생성된 랜덤코드와 동일한 joinCode를 가진 게임 방이 있는지 조회
         *      2.2. 충돌 시 1번 로직으로 이동
         *      2.3. 통과시 해당 랜덤코드를 새로 생성할 방의 joinCode로 채택
         *
         * conflict가 10번 초과하여 발생할 시 joinCode 생성 중 문제가 발생했다는 문구와 함께 잠시후 재시도 해달라는 문구 출력 필요
         */
        String joinCode = verifyJoinCode(JoinCodeGenerator.generate(joinCodeLength), joinCodeMaxConflictCount);


        /**
         * GameRoom 생성
         */
        return gameRoomRepository.save(
                GameRoom.create(joinCode, gameProperties.gameRoomCapacity())
        );
    }

// ==========

    /**
     * HOST의 요청으로 게임 세션을 시작한다.
     *
     * @param command 게임 시작 커맨드
     * @return 시작된 게임 session id
     */
    @Transactional
    public Long loadGameSession(StartGameCommand command) {
        log.debug("게임 시작 요청 ::: userId = {} => gameRoomId = {}", command.userId(), command.gameRoomId());
        GameRoom gameRoom = gameRoomRepository.findGameRoomById(command.gameRoomId()).orElseThrow(GameRoomNotFoundException::new);

        /**
         * 요청 보낸 유저가 대상 게임 방의 HOST가 맞는지 확인
         */
        gameRoom.validateIsHost(command.userId());


        /**
         * 새로운 게임 세션을 생성해 로드한다.
         */
        gameRoom.loadGameSession(command.timePerTurn());
        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);

        List<GameRoomParticipant> participants = gameRoom.getCurrentParticipants();
        userCommandPort.moveUsersToGameSession(participants.stream().map(GameRoomParticipant::getUserId).collect(Collectors.toList()));

        List<GamePlayHistory> gamePlayHistories = participants
                .stream()
                .map(participant -> new GamePlayHistory(participant.getUserId(), gameRoom.getCurrentGameSession().getId()))
                .toList();
        gamePlayHistoryRepository.saveAll(gamePlayHistories);


        /**
         * 이벤트 발행하여 커밋 이후 next turn 및 메세징 처리
         */
        gameRoom.processEventQueue(eventPublisher::publishEvent);

        return savedGameRoom.getCurrentGameSession().getId();
    }


    /**
     * 현재 게임 세션을 조회한다.
     *
     * @param gameRoomId
     * @return
     */
    @Transactional(readOnly = true)
    public GameSessionDto findCurrentGameSession(Long gameRoomId) {
        GameRoom gameRoom = gameRoomRepository.findGameRoomById(gameRoomId).orElseThrow(GameRoomNotFoundException::new);
        GameSession gameSession = gameRoom.getCurrentGameSession();

        return new GameSessionDto(
                gameSession.getId(),
                gameSession.getState(),
                gameSession.getTimePerTurn(),
                gameSession.getCompletedGameTurnCount(),
                gameSession.getTurnCount()
        );
    }

    /**
     * 현재 진행중인 게임 턴을 조회한다.
     *
     * @param gameSessionId
     * @return
     */
    @Transactional(readOnly = true)
    public GameTurnDto findCurrentGameTurn(Long gameSessionId, Long userId) {
        GameRoom gameRoom = gameRoomRepository.findGameRoomByGameSessionId(gameSessionId).orElseThrow(GameRoomNotFoundException::new);
        GameSession gameSession = gameRoom.getCurrentGameSession();
        GameTurn gameTurn = gameSession.getCurrentTurn();

        return new GameTurnDto(
                gameTurn.getDrawerId(),
                gameTurn.getDrawerId().equals(userId) ? gameTurn.getAnswer() : null,
                gameTurn.calculateExpiration());
    }


    /**
     * 다음 턴으로 진행한다.
     * 해당 메소드는 startGame 등과 같은 메소드 이후 시점에 실행되어야 하나
     * 별도의 컨텍스트에서 싫랭되어야 하므로 트랜잭션읇 분리한다.
     */
    @Transactional
    public void processToNextTurn(ProcessToNextTurnCommand command) {
        log.debug("{} 세션 다음 턴으로 진행", command.gameSessionId());
        GameRoom gameRoom = gameRoomRepository.findGameRoomByGameSessionId(command.gameSessionId()).orElseThrow(GameRoomNotFoundException::new);
        GameSession gameSession = gameRoom.getCurrentGameSession();

        if (!command.answered()) {
            gameSession.passCurrentTurn();
        }

        if (gameSession.shouldEnd()) {
            log.debug("모든 턴이 진행되었습니다.");
            log.debug("게임 세션 종료");

            gameRoom.endCurrentGameSession();
            gameRoomRepository.save(gameRoom);

            userCommandPort.moveUsersToRoom(gameRoom.getCurrentParticipants().stream().map(GameRoomParticipant::getUserId).collect(Collectors.toList()));

            gameRoom.processEventQueue(eventPublisher::publishEvent);
            return;
        }


        gameSession.allocateNewGameTurn(
                gameRoom.findNextDrawerId(),
                dictionaryQueryPort.drawRandomKoreanNoun()
        );

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);
        GameTurn newGameTurn = savedGameRoom.getGameSession().getCurrentTurn();

        /**
         * 새 턴이 진행되었음을 알리는 event pub
         */
        eventPublisher.publishEvent(
                new GameTurnProgressedEvent(
                        gameSession.getGameRoomId(),
                        command.gameSessionId(),
                        newGameTurn.getId()
                )
        );
    }

    /**
     * 게임 세션 토픽 구독에 성공했다고 상태에 기록하고,
     * 게임 세션 내의 유저들이 모두 로딩 상태가 되면 턴타이머 시작 이벤트를 발행
     *
     * @param userId
     */
    @Transactional
    public boolean successSubscription(Long gameSessionId, Long userId) {
        log.debug("user {} subscribe game session {}", userId, gameSessionId);
        GameRoom gameRoom = gameRoomRepository.findGameRoomByGameSessionId(gameSessionId).orElseThrow(GameRoomNotFoundException::new);
        int enteredUserCount = gameRoom.getCurrentParticipants().size();

        gameSessionLoadRegistry.register(gameSessionId, userId);
        if (gameSessionLoadRegistry.isAllLoaded(gameSessionId, enteredUserCount)) {
            log.debug("game session {} all loaded", gameSessionId);
            gameSessionLoadRegistry.clear(gameSessionId);

            gameRoom.startGameSession();
            gameRoomRepository.save(gameRoom);

            gameRoom.processEventQueue(eventPublisher::publishEvent);
            return true;
        }

        return false;
    }
}
