package com.bb.webcanvasservice.game.application.service;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.game.application.command.EnterGameRoomCommand;
import com.bb.webcanvasservice.game.application.command.ExitGameRoomCommand;
import com.bb.webcanvasservice.game.application.command.UpdateReadyCommand;
import com.bb.webcanvasservice.game.application.config.GameProperties;
import com.bb.webcanvasservice.game.application.dto.*;
import com.bb.webcanvasservice.game.application.port.dictionary.DictionaryQueryPort;
import com.bb.webcanvasservice.game.application.port.user.UserCommandPort;
import com.bb.webcanvasservice.game.application.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.event.GameRoomJoinEvent;
import com.bb.webcanvasservice.game.domain.exception.*;
import com.bb.webcanvasservice.game.domain.model.gameroom.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantRole.HOST;
import static com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState.WAITING;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoomService {
    /**
     * 크로스 도메인 포트
     */
    private final DictionaryQueryPort dictionaryQueryPort;
    private final UserCommandPort userCommandPort;

    /**
     * 도메인 레포지토리
     */
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomEntranceRepository gameRoomEntranceRepository;

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
        return enterGameRoom(new EnterGameRoomCommand(gameRoom.getId(), userId, HOST));
    }

    /**
     * 입장 가능한 게임 방을 조회해 리턴한다.
     * <p>
     * 이미 입장한 방이 있는 경우, AlreadyEnteredRoomException 을 throw한다.
     * <p>
     * TODO 메모리에서 WAITING GameRoomEntrance filtering 처리 -> batch size 및 페이징 처리 필요
     *
     * @param userId 유저 ID
     * @return GameRoomListResponse 게임 방 조회 응답 DTO
     */
    @Transactional
    public GameRoomListDto findJoinableGameRooms(Long userId) {
        return new GameRoomListDto(
                gameRoomRepository.findGameRoomsByCapacityAndStateWithEntranceState(gameProperties.gameRoomCapacity(), GameRoomState.enterable(), WAITING)
                        .stream()
                        .map(gameRoom ->
                                new GameRoomInfoDto(
                                        gameRoom.getId(),
                                        gameProperties.gameRoomCapacity(),
                                        (int) gameRoomEntranceRepository.findGameRoomEntranceCountByGameRoomIdAndState(gameRoom.getId(), WAITING),
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
     * - 입장하려는 방에 접속한 유저 세션의 수(entrances)는 최대 8이다.
     *
     * @param command 게임 방 입장 커맨드
     * @return gameRoomParticipantId 게임 방 입장 ID
     */
    @Transactional
    public GameRoomJoinDto enterGameRoom(EnterGameRoomCommand command) {
        /**
         * 대상 게임 방에 입장할 수 있는지 체크한다.
         * TODO 1. GameRoomUserQueryPort를 통한 유저 상태 체크
         * 2. 게임 방이 입장 가능한 상태인지 체크
         */
        GameRoom gameRoom = gameRoomRepository.findGameRoomById(command.gameRoomId()).orElseThrow(GameRoomNotFoundException::new);
        gameRoom.checkCanJoin();

        /**
         * dictionary 도메인 서비스로부터 랜덤 형용사 조회
         * 명사와 결합하여 랜덤 닉네임 할당
         *
         * HOST로 입장한다면 entrance의 ready상태를 true로 초기 설정
         */
        String koreanAdjective = dictionaryQueryPort.drawRandomKoreanAdjective();

        GameRoomParticipant newGameRoomParticipant = gameRoomEntranceRepository.save(
                GameRoomParticipant.create(
                        gameRoom.getId()
                        , command.userId()
                        , koreanAdjective
                        , command.role()
                )
        );

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

        return new GameRoomJoinDto(
                newGameRoomParticipant.getGameRoomId(),
                newGameRoomParticipant.getId()
        );
    }

    /**
     * Join Code로 게임 방에 입장한다.
     *
     * @param joinCode
     * @param userId
     * @return
     */
    @Transactional
    public GameRoomJoinDto enterGameRoomWithJoinCode(String joinCode, Long userId) {
        GameRoom targetGameRoom = gameRoomRepository.findRoomWithJoinCodeForEnter(joinCode)
                .orElseThrow(() -> new GameRoomNotFoundException(String.format("입장 코드가 %s인 방을 찾지 못했습니다.", joinCode)));

        return enterGameRoom(
                new EnterGameRoomCommand(targetGameRoom.getId(), userId, GameRoomParticipantRole.GUEST)
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
        GameRoomParticipant requestedParticipant = gameRoomEntranceRepository.findCurrentEnteredGameRoomEntranceByUserId(userId)
                .orElseThrow(GameRoomParticipantNotFoundException::new);

        AtomicInteger index = new AtomicInteger(0);

        List<GameRoomParticipant> gameRoomParticipants = gameRoomEntranceRepository
                .findGameRoomEntrancesByGameRoomIdAndStates(requestedParticipant.getGameRoomId(), GameRoomParticipantState.entered);

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

        GameRoom gameRoom = gameRoomRepository.findGameRoomById(requestedParticipant.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new);

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
     * @param gameRoomParticipantId
     * @param userId
     * @param ready
     * @return
     */
    @Transactional
    public boolean updateReady(UpdateReadyCommand command) {
        GameRoom gameRoom = gameRoomRepository.findGameRoomByGameRoomParticipantId(command.gameRoomParticipantId()).orElseThrow(GameRoomParticipantNotFoundException::new);

        gameRoom.changeParticipantReady(command.gameRoomParticipantId(), command.ready());

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
     * @param userId 유저 ID
     * @param joinCodeLength 입장 코드 최대 길이
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
        return gameRoomRepository.save(GameRoom.create(joinCode));
    }

    /**
     * 게임 방 ID와 게임 방 입장 상태에 맞는 게임 방 입장 목록을 조회해온다.
     *
     * @param gameRoomId               대상 게임 방 ID
     * @param gameRoomParticipantState 게임 방 입장 상태
     * @return 게임 방 입장 목록
     */
    @Transactional(readOnly = true)
    public List<GameRoomParticipant> findGameRoomEntrancesByGameRoomIdAndState(Long gameRoomId, GameRoomParticipantState gameRoomParticipantState) {
        return gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdAndState(gameRoomId, gameRoomParticipantState);
    }

    /**
     * 파라미터로 전달 받은 state에 있는 GameRoom 목록을 조회한다.
     *
     * @param state 게임 방의 상태
     * @return gameRoomList 게임 방 Entity List
     */
    @Transactional(readOnly = true)
    public List<GameRoom> findRoomsOnState(GameRoomState state) {
        return gameRoomRepository.findByState(state);
    }

    /**
     * 게임 방 입장 여부를 확인한다.
     *
     * @param gameRoomId 대상 게임 방 ID
     * @param userId     유저 ID
     * @return 게임 방 입장 여부
     */
    @Transactional(readOnly = true)
    public boolean isEnteredRoom(Long gameRoomId, Long userId) {
        return gameRoomEntranceRepository.existsActiveEntrance(gameRoomId, userId);
    }

    /**
     * 게임 방을 찾는다.
     *
     * @param gameRoomId 게임 방 ID
     * @return 게임 방
     */
    @Transactional(readOnly = true)
    public GameRoom findGameRoom(Long gameRoomId) {
        return gameRoomRepository.findGameRoomById(gameRoomId)
                .orElseThrow(GameRoomNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public void validateIsHost(Long gameRoomId, Long userId) {
        GameRoomParticipant userEntrance = gameRoomEntranceRepository.findCurrentEnteredGameRoomEntranceByUserId(userId)
                .orElseThrow(GameRoomParticipantNotFoundException::new);

        if (!userEntrance.isHost()) {
            throw new AbnormalAccessException();
        }
    }

    /**
     * 대상 게임 방 입장 정보의 상태를 초기화한다.
     *
     * @param gameRoomEntranceIds 게임 방 입장 정보 ID 목록
     */
    @Transactional
    public void resetGameRoomEntrances(List<Long> gameRoomEntranceIds) {
        gameRoomEntranceRepository.updateGameRoomEntrancesState(gameRoomEntranceIds, GameRoomParticipantState.WAITING);
    }

    /**
     * 대상 게임 방 입장 정보의 레디 상태를 초기화한다.
     *
     * @param gameRoomEntranceIds
     */
    @Transactional
    public void resetGameRoomEntrancesReady(List<Long> gameRoomEntranceIds) {
        List<GameRoomParticipant> gameRoomParticipants = gameRoomEntranceRepository.findGameRoomEntrancesByIds(gameRoomEntranceIds);
        gameRoomParticipants.forEach(GameRoomParticipant::resetReady);
        gameRoomEntranceRepository.saveAll(gameRoomParticipants);
    }
}
