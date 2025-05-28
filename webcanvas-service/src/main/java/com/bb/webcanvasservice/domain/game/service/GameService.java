package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.domain.game.dto.request.GameStartRequest;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceInfoResponse;
import com.bb.webcanvasservice.domain.game.entity.*;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.event.GameSessionEndEvent;
import com.bb.webcanvasservice.domain.game.event.GameSessionStartEvent;
import com.bb.webcanvasservice.domain.game.event.GameTurnProgressedEvent;
import com.bb.webcanvasservice.domain.game.exception.*;
import com.bb.webcanvasservice.domain.game.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import com.bb.webcanvasservice.domain.game.repository.GameTurnRepository;
import com.bb.webcanvasservice.domain.user.service.UserService;
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

import static com.bb.webcanvasservice.domain.game.enums.GameSessionState.COMPLETED;
import static com.bb.webcanvasservice.domain.game.enums.GameSessionState.PLAYING;

/**
 * 게임 세션 및 플레이 관련 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {


    private final GameSessionRepository gameSessionRepository;
    private final GamePlayHistoryRepository gamePlayHistoryRepository;
    private final GameTurnRepository gameTurnRepository;

    private final DictionaryService dictionaryService;
    private final UserService userService;
    private final GameRoomFacade gameRoomFacade;

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

        GameRoom gameRoom = gameRoomFacade.findGameRoom(request.gameRoomId());
        GameRoomEntranceInfoResponse enteredGameRoomInfo = gameRoomFacade.findEnteredGameRoomInfo(userId);

        /**
         * 요청 보낸 유저가 HOST가 맞는지 확인
         */
        enteredGameRoomInfo
                .enteredUsers()
                .stream()
                .filter(user -> user.role().equals(GameRoomRole.HOST) && user.userId().equals(userId))
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

        GameSession gameSession = new GameSession(gameRoom, request.turnCount(), request.timePerTurn());
        /**
         * 입장 정보의 state를 PLAYING으로 변경하고, GamePlayHistory entity로 매핑
         * startGame 처리중 exit하는 유저와의 동시성 문제를 막고자 lock을 걸어 조회한다.
         */
        List<GamePlayHistory> gamePlayHistories = gameRoomFacade.findCurrentGameRoomEntrancesWithLock(gameRoom.getId())
                .stream()
                .map(entrance -> {
                    entrance.changeState(GameRoomEntranceState.PLAYING);
                    return new GamePlayHistory(entrance.getUser(), gameSession);
                })
                .collect(Collectors.toList());

        /**
         * 게임 방 상태를 변경
         */
        gameRoom.readyToPlay();

        /**
         * 새로 생성된 Entity 저장
         *
         * - GameSession
         * - 방에 입력한 유저들의 게임 플레이 히스토리 저장
         */
        gameSessionRepository.save(gameSession);
        gamePlayHistoryRepository.saveAll(gamePlayHistories);

        /**
         * 이벤트 발행하여 커밋 이후 next turn 및 메세징 처리
         */
        applicationEventPublisher.publishEvent(new GameSessionStartEvent(gameRoom.getId(), gameSession.getId()));

        return gameSession.getId();
    }

    /**
     * 게임 세션에 포함된 턴 목록을 조회한다.
     *
     * @param gameSessionId
     * @return
     */
    @Transactional(readOnly = true)
    public List<GameTurn> findTurnsInGameSession(Long gameSessionId) {
        return gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new)
                .getGameTurns();
    }

    /**
     * 게임 세션을 조회한다.
     *
     * @param gameSessionId
     * @return
     */
    @Transactional(readOnly = true)
    public GameSession findGameSession(Long gameSessionId) {
        return gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);
    }

    /**
     * 현재 라운드를 조회한다.
     *
     * @param gameSessionId
     * @return 현재 게임 세션의 라운드
     */
    @Transactional(readOnly = true)
    public int findCurrentRound(Long gameSessionId) {
        return gameSessionRepository.findCurrentRound(gameSessionId);
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
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        if (gameSession.getState() != PLAYING) {
            log.debug("게임 세션이 PLAYING 상태가 아닙니다.");
            throw new GameSessionIsOverException();
        }

        List<GameTurn> gameTurns = gameSession.getGameTurns();
        if (gameSession.getTurnCount() <= gameTurns.size()) {
            log.debug("현재 세션에 준비된 턴이 모두 끝났습니다.");
            log.debug("현재 사용된 턴 = {}", gameTurns.size());
            log.debug("현재 세션에 준비된 턴 = {}", gameSession.getTurnCount());
            throw new GameSessionIsOverException();
        }

        /**
         * 현재 게임중인 유저 목록
         */
        List<GameRoomEntrance> gameRoomEntrances = gameRoomFacade.findGameRoomEntrancesByGameRoomIdAndState(gameSession.getGameRoom().getId(), GameRoomEntranceState.PLAYING);

        // 유저별 턴 수 집계
        Map<Long, Integer> drawerCountMap = gameTurns.stream()
                .collect(Collectors.toMap(
                        gt -> gt.getDrawer().getId(),
                        gt -> 1,
                        Integer::sum
                ));

        int minCount = Integer.MAX_VALUE;
        List<Long> candidates = new ArrayList<>();

        for (GameRoomEntrance entrance : gameRoomEntrances) {
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
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        if (gameSession.getState() != PLAYING) {
            log.debug("진행중인 게임 세션이 아닙니다. ====== {} : {}", gameSession, gameSession.getState());
            throw new GameSessionIsOverException();
        }

        if (gameSession.getGameTurns().size() >= gameSession.getTurnCount()) {
            log.debug("모든 턴이 진행되었습니다.");
            log.debug("게임 세션 종료");

            applicationEventPublisher.publishEvent(new GameSessionEndEvent(gameSessionId, gameSession.getGameRoom().getId()));
            return;
        }


        /**
         * @param GameSession gameSession
         * @param User drawer
         * @Param String answer
         */
        GameTurn gameTurn = gameTurnRepository.save(
                new GameTurn(
                        gameSession,
                        userService.findUser(findNextDrawerId(gameSessionId)),
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
     * 게임을 종료한다.
     * @param gameSessionId
     */
    @Transactional
    public void endGame(Long gameSessionId) {
        if (isGameEnd(gameSessionId)) {
            log.debug("이미 게임 세션이 종료되었습니다.");
            throw new GameSessionIsOverException();
        }
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        /**
         * 내부에서만 요청되는 메소드로 클라이언트 validation 처리 없이 complete한다.
         */
        GameRoom gameRoom = gameSession.getGameRoom();

        List<GameRoomEntrance> currentPlayingEntrances = gameRoomFacade.findGameRoomEntrancesByGameRoomIdAndState(gameRoom.getId(), GameRoomEntranceState.PLAYING);
        currentPlayingEntrances.stream()
                .forEach(gameRoomEntrance -> gameRoomEntrance.changeState(GameRoomEntranceState.WAITING));

        gameSession.end();

        applicationEventPublisher.publishEvent(new GameSessionEndEvent(gameSessionId, gameRoom.getId()));
    }

    /**
     * 게임 종료인지 여부를 체크한다.
     * @param gameSessionId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isGameEnd(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        return gameSession.getState() == COMPLETED
                || gameSession.getGameTurns().size() >= gameSession.getTurnCount();
    }

    /**
     * 정답인지 체크한다.
     * @param gameTurnId
     * @param answer
     * @return
     */
    @Transactional
    public boolean isAnswer(Long gameTurnId, String answer) {
        return gameTurnRepository
                .findById(gameTurnId).orElseThrow(GameTurnNotFoundException::new)
                .getAnswer().equals(answer);
    }
}
