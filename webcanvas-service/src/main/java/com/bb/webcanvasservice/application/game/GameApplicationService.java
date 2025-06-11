package com.bb.webcanvasservice.application.game;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.domain.game.event.AllUserInGameSessionLoadedEvent;
import com.bb.webcanvasservice.domain.game.event.GameSessionEndEvent;
import com.bb.webcanvasservice.domain.game.event.GameSessionStartEvent;
import com.bb.webcanvasservice.domain.game.event.GameTurnProgressedEvent;
import com.bb.webcanvasservice.domain.game.exception.*;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceRole;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.model.GameRoomState;
import com.bb.webcanvasservice.domain.game.registry.GameSessionLoadRegistry;
import com.bb.webcanvasservice.domain.game.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.domain.user.model.UserStateCode;
import com.bb.webcanvasservice.domain.user.service.UserService;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.*;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameSessionJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameTurnJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.user.UserModelMapper;
import com.bb.webcanvasservice.presentation.game.request.GameStartRequest;
import com.bb.webcanvasservice.presentation.game.response.GameRoomEntranceInfoResponse;
import com.bb.webcanvasservice.presentation.game.response.GameSessionResponse;
import com.bb.webcanvasservice.presentation.game.response.GameTurnFindResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import static com.bb.webcanvasservice.domain.game.model.GameSessionState.PLAYING;

/**
 * 게임 세션 및 플레이 관련 application layer service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameApplicationService {
    private final GameSessionJpaRepository gameSessionRepository;
    private final GamePlayHistoryRepository gamePlayHistoryRepository;
    private final GameTurnJpaRepository gameTurnRepository;
    private final GameRoomJpaRepository gameRoomRepository;

    private final DictionaryService dictionaryService;
    private final UserService userService;

    private final GameSessionLoadRegistry gameSessionLoadRegistry;

    private final ApplicationEventPublisher applicationEventPublisher;


    /**
     * 게임을 시작한다.
     *
     * @param request
     * @param userId
     * @return
     */
    @Transactional
    public Long startGame(GameStartRequest request, Long userId) {
        log.debug("게임 시작 요청 ::: userId = {} => gameRoomId = {}", userId, request.gameRoomId());

        GameRoomJpaEntity gameRoom = gameRoomFacade.findGameRoom(request.gameRoomId());
        GameRoomEntranceInfoResponse enteredGameRoomInfo = gameRoomFacade.findEnteredGameRoomInfo(userId);

        /**
         * 요청 보낸 유저가 HOST가 맞는지 확인
         */
        enteredGameRoomInfo
                .enteredUsers()
                .stream()
                .filter(user -> user.role().equals(GameRoomEntranceRole.HOST) && user.userId().equals(userId))
                .findFirst()
                .ifPresentOrElse(info -> {
                    log.debug("호스트 확인 userId = {}", userId);
                }, () -> {
                    throw new AbnormalAccessException();
                });

        /**
         * 게임 방 상태 확인
         */
        if (gameRoom.getState() != GameRoomState.WAITING) {
            log.debug("게임 방의 상태가 게임을 시작할 수 없는 상태입니다. ====== {}", gameRoom.getState());
            throw new IllegalGameRoomStateException();
        }

        GameSessionJpaEntity gameSession = new GameSessionJpaEntity(gameRoom, request.turnCount(), request.timePerTurn());
        /**
         * 입장 정보의 state를 PLAYING으로 변경하고, GamePlayHistory entity로 매핑
         * 유저 상태 변경
         * startGame 처리중 exit하는 유저와의 동시성 문제를 막고자 lock을 걸어 조회한다.
         *
         * 250531 게임 시작시 레디상태 false로 모두 변경
         */
        List<GamePlayHistoryJpaEntity> gamePlayHistories = gameRoomFacade.findCurrentGameRoomEntrancesWithLock(gameRoom.getId())
                .stream()
                .map(entrance -> {
                    entrance.getUser().changeState(UserStateCode.IN_GAME);

                    if (entrance.getRole() == GameRoomEntranceRole.GUEST) {
                        entrance.changeReady(false);
                    }

                    return new GamePlayHistoryJpaEntity(entrance.getUser(), gameSession);
                })
                .collect(Collectors.toList());

        /**
         * 게임 방 상태를 변경
         */
        gameRoom.changeStateToPlay();

        /**
         * 새로 생성된 Entity 저장
         *
         * - GameSession
         * - 방에 입력한 유저들의 게임 플레이 히스토리 저장
         */
        gameSessionRepository.save(gameSession);

        /**
         * TODO 서비스 메소드 분리 필요
         */
//        gamePlayHistoryRepository.saveAll(gamePlayHistories);


        /**
         * 이벤트 발행하여 커밋 이후 next turn 및 메세징 처리
         */
        applicationEventPublisher.publishEvent(new GameSessionStartEvent(gameRoom.getId(), gameSession.getId()));

        return gameSession.getId();
    }

    /**
     * 게임 세션을 조회한다.
     *
     * @param gameSessionId
     * @return
     */
    @Transactional(readOnly = true)
    public GameSessionJpaEntity findGameSession(Long gameSessionId) {
        return gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);
    }

    /**
     * 게임 세션에 유저가 참여해 있는지 여부를 체크한다.
     *
     * @param gameSessionId
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean inGameSession(Long gameSessionId, Long userId) {
        GameSessionJpaEntity gameSession = findGameSession(gameSessionId);
        return gameRoomFacade.isEnteredRoom(gameSession.getGameRoom().getId(), userId);
    }


    /**
     * 현재 게임 세션을 조회한다.
     *
     * @param gameRoomId
     * @return
     */
    @Transactional(readOnly = true)
    public GameSessionResponse findCurrentGameSession(Long gameRoomId) {
        GameSessionJpaEntity gameSession = gameSessionRepository.findGameSessionsByGameRoomId(gameRoomId)
                .stream()
                .filter(GameSessionJpaEntity::isActive)
                .findFirst()
                .orElseThrow(GameSessionNotFoundException::new);

        gameTurnRepository.findTurnCountByGameSessionId(gameSession.getId());
        return new GameSessionResponse(
                gameSession.getId(),
                gameSession.getState(),
                gameSession.getTimePerTurn(),
                (int) gameTurnRepository.findTurnCountByGameSessionId(gameSession.getId()),
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
    public GameTurnFindResponse findCurrentGameTurn(Long gameSessionId, Long userId) {
        GameTurnJpaEntity gameTurn = gameTurnRepository.findLatestTurn(gameSessionId)
                .orElseThrow(GameTurnNotFoundException::new);

        if (!gameTurn.isActive()) {
            log.debug("활성화된 게임 턴이 아닙니다.");
            throw new GameTurnNotFoundException();
        }

        return new GameTurnFindResponse(
                gameTurn.getDrawer().getId(),
                gameTurn.getDrawer().getId().equals(userId) ? gameTurn.getAnswer() : null,
                gameTurn.getExpiration());
    }


    /**
     * TODO chat 메세지에서 검증 처리 추가
     * - chat 메세지 브로드캐스팅 전 게임 상태로 분기해 게임 진행중이면 정답 체크
     * - 정답과 동등성 비교 후 정답 hit시 정답 answer 이벤트 발행 후 브로드캐스팅
     * - 정답 처리 후 타이머 초기화 && nextTurn 호출
     */

    /**
     * 다음 차례로 그림을 그릴 유저 ID를 찾는다.
     *
     * @param gameSessionId
     * @return
     */
    @Transactional(readOnly = true)
    public Long findNextDrawerId(Long gameSessionId) {
        GameSessionJpaEntity gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        if (gameSession.getState() != PLAYING) {
            log.debug("게임 세션이 PLAYING 상태가 아닙니다.");
            throw new GameSessionIsOverException();
        }

        List<GameTurnJpaEntity> gameTurns = gameTurnRepository.findTurnsByGameSessionId(gameSession.getId());
        if (gameSession.getTurnCount() <= gameTurns.size()) {
            log.debug("현재 세션에 준비된 턴이 모두 끝났습니다.");
            log.debug("현재 사용된 턴 = {}", gameTurns.size());
            log.debug("현재 세션에 준비된 턴 = {}", gameSession.getTurnCount());
            throw new GameSessionIsOverException();
        }

        /**
         * 현재 게임중인 유저 목록
         */
        List<GameRoomEntranceJpaEntity> gameRoomEntrances = gameRoomFacade.findGameRoomEntrancesByGameRoomIdAndState(gameSession.getGameRoom().getId(), GameRoomEntranceState.PLAYING);

        // 유저별 턴 수 집계
        Map<Long, Integer> drawerCountMap = gameTurns.stream()
                .collect(Collectors.toMap(
                        gt -> gt.getDrawer().getId(),
                        gt -> 1,
                        Integer::sum
                ));

        int minCount = Integer.MAX_VALUE;
        List<Long> candidates = new ArrayList<>();

        for (GameRoomEntranceJpaEntity entrance : gameRoomEntrances) {
            Long userId = entrance.getUser().getId();
            int count = drawerCountMap.getOrDefault(userId, 0);

            if (count < minCount) {
                candidates.clear();
                candidates.add(userId);
                minCount = count;
            } else if (count == minCount) {
                candidates.add(userId);
            }
        }

        if (candidates.isEmpty()) {
            log.debug("후보자를 찾지 못했습니다.");
            throw new NextDrawerNotFoundException();
        }

        /**
         * 후보 ID들 중 랜덤 Index를 뽑아 리턴한다.
         */
        RandomGenerator randomGenerator = RandomGenerator.getDefault();
        int randomIndex = randomGenerator.nextInt(candidates.size());

        return candidates.get(randomIndex);
    }


    /**
     * 다음 턴으로 진행한다.
     * 해당 메소드는 startGame 등과 같은 메소드 이후 시점에 실행되어야 하나
     * 별도의 컨텍스트에서 싫랭되어야 하므로 트랜잭션읇 분리한다.
     */
    @Transactional
    public void processToNextTurn(Long gameSessionId) {
        log.debug("{} 세션 다음 턴으로 진행", gameSessionId);
        GameSessionJpaEntity gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        if (gameSession.isEnd()) {
            log.debug("진행중인 게임 세션이 아닙니다. ====== {} : {}", gameSession, gameSession.getState());
            throw new GameSessionIsOverException();
        }

        /**
         * 지난 턴이 아직 ACTIVE로 존재할 경우 pass한다.
         */
        gameTurnRepository.findLatestTurn(
                        gameSession.getId())
                .ifPresent(GameTurnJpaEntity::pass);

        if (shouldEnd(gameSession)) {
            log.debug("모든 턴이 진행되었습니다.");
            log.debug("게임 세션 종료");

            endGame(gameSessionId);
            return;
        }

        /**
         * @param GameSession gameSession
         * @param User drawer
         * @Param String answer
         */
        GameTurnJpaEntity gameTurn = gameTurnRepository.save(
                new GameTurnJpaEntity(
                        gameSession,
                        UserModelMapper.toUserJpaEntity(userService.findUser(findNextDrawerId(gameSessionId))),
                        dictionaryService.drawRandomWordValue(Language.KOREAN, PartOfSpeech.NOUN)
                ));

        /**
         * 새 턴이 진행되었음을 알리는 event pub
         */
        applicationEventPublisher.publishEvent(
                new GameTurnProgressedEvent(
                        gameSession.getGameRoom().getId(),
                        gameSessionId,
                        gameTurn.getId()
                )
        );
    }

    /**
     * 게임 턴이 모두 진행되어 게임을 종료상태로 변경해야하는지 체크한다.
     *
     * @return 게임을 종료해야되는지 여부
     */
    @Transactional(readOnly = true)
    public boolean shouldEnd(GameSessionJpaEntity gameSession) {
        List<GameTurnJpaEntity> turnsInSession = gameTurnRepository.findTurnsByGameSessionId(gameSession.getId());
        return turnsInSession.size() >= gameSession.getTurnCount();
    }

    /**
     * 게임을 종료한다.
     *
     * @param gameSessionId
     */
    @Transactional
    public void endGame(Long gameSessionId) {
        GameSessionJpaEntity gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        if (gameSession.isEnd()) {
            log.debug("이미 게임 세션이 종료되었습니다.");
            throw new GameSessionIsOverException();
        }
        log.debug("게임 세션 종료 ====== gameSessionId : {}", gameSessionId);


        /**
         * 내부에서만 요청되는 메소드로 클라이언트 validation 처리 없이 complete한다.
         */
        GameRoomJpaEntity gameRoom = gameSession.getGameRoom();

        List<GameRoomEntranceJpaEntity> currentPlayingEntrances = gameRoomFacade.findGameRoomEntrancesByGameRoomIdAndState(gameRoom.getId(), GameRoomEntranceState.PLAYING);
        currentPlayingEntrances.stream()
                .forEach(gameRoomEntrance -> {
                    gameRoomEntrance.resetGameRoomEntranceInfo();
                    gameRoomEntrance.getUser().endGameAndResetToRoom();
                });

        gameSession.end();

        applicationEventPublisher.publishEvent(new GameSessionEndEvent(gameSessionId, gameRoom.getId()));
    }


    /**
     * 게임 세션 토픽 구독에 성공했다고 상태에 기록하고,
     * 게임 세션 내의 유저들이 모두 로딩 상태가 되면 턴타이머 시작 이벤트를 발행
     *
     * @param userId
     */
    @Transactional
    public void successSubscription(Long gameSessionId, Long userId) {
        log.debug("success subscription = {}", gameSessionId);
        GameSessionJpaEntity gameSession = findGameSession(gameSessionId);
        if (gameSession.isPlaying()) {
            log.debug("이미 게임이 진행중입니다.");
            return;
        }

        GameRoomJpaEntity gameRoom = gameSession.getGameRoom();

        int enteredUserCount = gameRoomFacade.findEnteredUserCount(gameRoom.getId());
        gameSessionLoadRegistry.register(gameSessionId, userId);

        if (gameSessionLoadRegistry.isAllLoaded(gameSessionId, enteredUserCount)) {
            gameSession.start();

            gameRoomFacade
                    .findGameRoomEntrancesByGameRoomIdAndState(gameRoom.getId(), GameRoomEntranceState.WAITING)
                    .forEach(entrance -> entrance.changeState(GameRoomEntranceState.PLAYING));

            applicationEventPublisher.publishEvent(new AllUserInGameSessionLoadedEvent(gameSessionId, gameRoom.getId()));
            gameSessionLoadRegistry.clear(gameSessionId);
        }
    }
}
