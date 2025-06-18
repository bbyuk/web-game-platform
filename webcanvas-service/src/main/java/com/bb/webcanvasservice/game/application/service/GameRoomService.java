package com.bb.webcanvasservice.game.application.service;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.game.application.command.EnterGameRoomCommand;
import com.bb.webcanvasservice.game.application.config.GameProperties;
import com.bb.webcanvasservice.game.application.dto.*;
import com.bb.webcanvasservice.game.application.port.dictionary.DictionaryQueryPort;
import com.bb.webcanvasservice.game.application.port.user.UserCommandPort;
import com.bb.webcanvasservice.game.domain.event.GameRoomJoinEvent;
import com.bb.webcanvasservice.game.domain.event.GameRoomExitEvent;
import com.bb.webcanvasservice.game.domain.event.GameRoomHostChangedEvent;
import com.bb.webcanvasservice.game.domain.event.UserReadyChanged;
import com.bb.webcanvasservice.game.domain.exception.*;
import com.bb.webcanvasservice.game.application.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomState;
import com.bb.webcanvasservice.game.domain.model.participant.GameRoomParticipant;
import com.bb.webcanvasservice.game.domain.model.participant.GameRoomParticipantRole;
import com.bb.webcanvasservice.game.domain.model.participant.GameRoomParticipantState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.bb.webcanvasservice.game.domain.model.participant.GameRoomParticipantRole.HOST;
import static com.bb.webcanvasservice.game.domain.model.participant.GameRoomParticipantState.WAITING;

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
        GameRoom gameRoom = createGameRoom(userId, gameProperties.joinCodeLength(), gameProperties.joinCodeMaxConflictCount());
        return enterGameRoom(new EnterGameRoomCommand(gameRoom.getId(), userId, HOST));
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
         */

        checkGameRoomCanEnter(command.gameRoomId(), command.userId(), command.role(), gameProperties.gameRoomCapacity());

        /**
         * dictionary 도메인 서비스로부터 랜덤 형용사 조회
         * 명사와 결합하여 랜덤 닉네임 할당
         *
         * HOST로 입장한다면 entrance의 ready상태를 true로 초기 설정
         */
        String koreanAdjective = dictionaryQueryPort.drawRandomKoreanAdjective();

        GameRoomParticipant newGameRoomParticipant = gameRoomEntranceRepository.save(
                new GameRoomParticipant(
                        null
                        , command.gameRoomId()
                        , command.userId()
                        , GameRoomParticipantState.WAITING
                        , String.format("%s %s", koreanAdjective, "플레이어")
                        , command.role()
                        , command.role() == GameRoomParticipantRole.HOST
                )
        );

        userCommandPort.moveUserToRoom(command.userId());

        /**
         * 게임 방 입장 이벤트 pub ->
         * 게임 방 broker에 입장 send 위임
         */
        eventPublisher.publishEvent(new GameRoomJoinEvent(command.gameRoomId(), command.userId()));

        return new GameRoomJoinDto(newGameRoomParticipant.getGameRoomId(), newGameRoomParticipant.getId());
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
    public GameRoomListDto findEnterableGameRooms(Long userId) {
        checkUserCanEnterGameRoom(userId);

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
    public GameRoomJoinDetailInfoDto findEnteredGameRoomInfo(Long userId) {
        GameRoomParticipant userEntrance = gameRoomEntranceRepository.findCurrentEnteredGameRoomEntranceByUserId(userId)
                .orElseThrow(GameRoomParticipantNotFoundException::new);

        AtomicInteger index = new AtomicInteger(0);


        List<GameRoomParticipant> gameRoomParticipants = gameRoomEntranceRepository
                .findGameRoomEntrancesByGameRoomIdAndStates(userEntrance.getGameRoomId(), GameRoomParticipantState.entered);

        List<JoinedUserInfoDto> enteredUserSummaries = gameRoomParticipants
                .stream()
                .map(gameRoomEntrance ->
                        new JoinedUserInfoDto(
                                gameRoomEntrance.getUserId(),
                                gameProperties.gameRoomUserColors().get(index.getAndIncrement()),
                                gameRoomEntrance.getNickname(),
                                userEntrance.getRole(),
                                gameRoomEntrance.isReady()
                        )
                )
                .collect(Collectors.toList());

        GameRoom gameRoom = gameRoomRepository.findById(userEntrance.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new);

        return new GameRoomJoinDetailInfoDto(
                userEntrance.getGameRoomId(),
                userEntrance.getId(),
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
     * @param gameRoomEntranceId 대상 게임 방 입장 ID
     * @param userId             유저 ID
     */
    @Transactional
    public void exitFromRoom(Long gameRoomEntranceId, Long userId) {
        GameRoomParticipant targetEntrance = gameRoomEntranceRepository.findById(gameRoomEntranceId)
                .orElseThrow(GameRoomParticipantNotFoundException::new);

        if (!targetEntrance.getUserId().equals(userId)) {
            log.error("대상 게임 방 입장 기록과 요청 유저가 다릅니다.");
            throw new AbnormalAccessException();
        }
        targetEntrance.exit();
        gameRoomEntranceRepository.save(targetEntrance);

        GameRoom gameRoom = gameRoomRepository.findById(targetEntrance.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new);

        List<GameRoomParticipant> entrances = gameRoomEntranceRepository
                .findGameRoomEntrancesByGameRoomIdAndState(gameRoom.getId(), GameRoomParticipantState.WAITING);

        if (entrances.isEmpty()) {
            gameRoom.close();
            gameRoomRepository.save(gameRoom);
        } else if (targetEntrance.getRole() == HOST) {
            /**
             * 250522
             * 퇴장 요청을 보낸 유저가 HOST일 경우 남은 유저 중 제일 처음 입장한 유저가 HOST가 된다.
             */
            entrances.stream()
                    .filter(entrance -> !entrance.getId().equals(gameRoomEntranceId))
                    .findFirst()
                    .ifPresentOrElse(
                            entrance -> {
                                entrance.changeRole(HOST);
                                gameRoomEntranceRepository.save(entrance);
                                // HOST changed event 발행

                                eventPublisher.publishEvent(new GameRoomHostChangedEvent(entrance.getGameRoomId(), entrance.getUserId()));
                            },
                            gameRoom::close
                    );
        }

        userCommandPort.moveUserToLobby(userId);

        /**
         * 250519 게임방 퇴장시 event 발행
         */
        eventPublisher.publishEvent(new GameRoomExitEvent(gameRoom.getId(), userId));
    }

    /**
     * 게임 방에 입장한 유저의 레디 값을 변경한다.
     *
     * @param gameRoomEntranceId
     * @param userId
     * @param ready
     * @return
     */
    @Transactional
    public boolean updateReady(Long gameRoomEntranceId, Long userId, boolean ready) {
        GameRoomParticipant targetEntrance = gameRoomEntranceRepository.findById(gameRoomEntranceId)
                .orElseThrow(GameRoomParticipantNotFoundException::new);

        if (!targetEntrance.getUserId().equals(userId)) {
            log.error("다른 유저의 정보 접근 시도");
            log.error("userId = {} ===>>> gameRoomParticipantId = {}", userId, gameRoomEntranceId);
            throw new AbnormalAccessException();
        }

        if (targetEntrance.getRole() == HOST) {
            log.debug("호스트는 레디 상태를 변경할 수 없습니다.");
            throw new GameRoomHostCanNotChangeReadyException();
        }

        targetEntrance.changeReady(ready);
        gameRoomEntranceRepository.save(targetEntrance);

        log.debug("게임 방 레디 변경 저장 = {}", targetEntrance.getId());

        eventPublisher.publishEvent(new UserReadyChanged(targetEntrance.getGameRoomId(), userId, ready));

        return ready;
    }


    /**
     * 게임 방 입장코드를 사용할 수 있는지 verify 하고 충돌 발생시 최대 충돌 가능 threshold까지 redraw하여 리턴한다.
     * @param joinCode 대상 입장코드
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

    @Transactional
    public GameRoom createGameRoom(Long userId, int joinCodeLength, int joinCodeMaxConflictCount) {
        /**
         * 유저가 새로 게임을 생성할 수 있는 상태인지 확인한다.
         * TODO UserService로 이관 필요
         * - 유저가 현재 아무 방에도 입장하지 않은 상태여야 한다.
         */
        checkUserCanEnterGameRoom(userId);

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

    @Transactional(readOnly = true)
    public void checkGameRoomCanEnter(Long gameRoomId, Long userId, GameRoomParticipantRole role, int gameRoomCapacity) {
        /**
         * 요청한 유저가 새로운 게임 방에 입장할 수 있는 상태인지 체크한다.
         * TODO UserService로 이관 필요
         */
        checkUserCanEnterGameRoom(userId);

        GameRoom targetGameRoom = gameRoomRepository.findById(gameRoomId).orElseThrow(GameRoomNotFoundException::new);
        if (!targetGameRoom.isWaiting()) {
            throw new IllegalGameRoomStateException();
        }

        List<GameRoomParticipant> targetGameRoomParticipants = gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdAndState(gameRoomId, GameRoomParticipantState.WAITING);

        int enteredUserCounts = targetGameRoomParticipants.size();

        if (enteredUserCounts >= gameRoomCapacity) {
            throw new IllegalGameRoomStateException("방의 정원이 모두 찼습니다.");
        }
    }

    /**
     * 유저가 게임 방에 입장할 수 있는 상태인지 확인한다.
     * @param userId 대상 유저 ID
     */
    @Transactional(readOnly = true)
    public void checkUserCanEnterGameRoom(Long userId) {
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(userId)) {
            throw new AlreadyEnteredRoomException();
        }
    }

    /**
     * 게임 방 ID와 게임 방 입장 상태에 맞는 게임 방 입장 목록을 조회해온다.
     *
     * @param gameRoomId 대상 게임 방 ID
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
     * @param userId 유저 ID
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
        return gameRoomRepository.findById(gameRoomId)
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
     * @param gameRoomEntranceIds 게임 방 입장 정보 ID 목록
     */
    @Transactional
    public void resetGameRoomEntrances(List<Long> gameRoomEntranceIds) {
        gameRoomEntranceRepository.updateGameRoomEntrancesState(gameRoomEntranceIds, GameRoomParticipantState.WAITING);
    }

    /**
     * 대상 게임 방 입장 정보의 레디 상태를 초기화한다.
     * @param gameRoomEntranceIds
     */
    @Transactional
    public void resetGameRoomEntrancesReady(List<Long> gameRoomEntranceIds) {
        List<GameRoomParticipant> gameRoomParticipants = gameRoomEntranceRepository.findGameRoomEntrancesByIds(gameRoomEntranceIds);
        gameRoomParticipants.forEach(GameRoomParticipant::resetReady);
        gameRoomEntranceRepository.saveAll(gameRoomParticipants);
    }
}
