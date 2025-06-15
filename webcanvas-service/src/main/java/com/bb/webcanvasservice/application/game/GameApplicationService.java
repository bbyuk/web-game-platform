package com.bb.webcanvasservice.application.game;

import com.bb.webcanvasservice.application.game.command.StartGameCommand;
import com.bb.webcanvasservice.application.game.dto.GameSessionDto;
import com.bb.webcanvasservice.application.game.dto.GameTurnDto;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.domain.game.event.AllUserInGameSessionLoadedEvent;
import com.bb.webcanvasservice.domain.game.event.GameSessionEndEvent;
import com.bb.webcanvasservice.domain.game.event.GameSessionStartEvent;
import com.bb.webcanvasservice.domain.game.event.GameTurnProgressedEvent;
import com.bb.webcanvasservice.domain.game.exception.GameSessionIsOverException;
import com.bb.webcanvasservice.domain.game.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.GameTurnNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.domain.game.model.*;
import com.bb.webcanvasservice.domain.game.registry.GameSessionLoadRegistry;
import com.bb.webcanvasservice.domain.game.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import com.bb.webcanvasservice.domain.game.service.GameRoomService;
import com.bb.webcanvasservice.domain.game.service.GameService;
import com.bb.webcanvasservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게임 세션 및 플레이 관련 application layer service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameApplicationService {

    /**
     * 도메인 서비스
     */
    private final GameRoomService gameRoomService;
    private final GameService gameService;

    /**
     * 도메인 레포지토리
     */
    private final GameSessionRepository gameSessionRepository;
    private final GamePlayHistoryRepository gamePlayHistoryRepository;
    private final GameRoomEntranceRepository gameRoomEntranceRepository;
    private final GameRoomRepository gameRoomRepository;

    /**
     * 크로스도메인 서비스
     */
    private final DictionaryService dictionaryService;
    private final UserService userService;

    /**
     * common
     */
    private final GameSessionLoadRegistry gameSessionLoadRegistry;
    private final ApplicationEventPublisher applicationEventPublisher;


    /**
     * 게임을 시작한다.
     *
     * @param command 게임 시작 커맨드
     * @return 시작된 게임 session id
     */
    @Transactional
    public Long startGame(StartGameCommand command) {
        log.debug("게임 시작 요청 ::: userId = {} => gameRoomId = {}", command.userId(), command.gameRoomId());
        GameRoom gameRoom = gameRoomService.findGameRoom(command.gameRoomId());

        /**
         * 요청 보낸 유저가 대상 게임 방의 HOST가 맞는지 확인
         */
        gameRoomService.validateIsHost(command.gameRoomId(), command.userId());

        /**
         * 게임 방 상태 확인
         */
        if (!gameRoom.isWaiting()) {
            String message = "게임 방의 상태가 게임을 시작할 수 없는 상태입니다.";
            log.debug("{} ====== {}", message, gameRoom.getState());
            throw new IllegalGameRoomStateException(message);
        }
        /**
         * 현재 진행중인 게임 세션이 있는지 확인
         */
        boolean hasActiveGameSession = gameSessionRepository
                .findGameSessionsByGameRoomId(command.gameRoomId())
                .stream()
                .anyMatch(GameSession::isActive);
        if (hasActiveGameSession) {
            String message = "이미 게임 세션이 진행중입니다.";
            log.debug("{} ====== {}", message, command.gameRoomId());
            throw new IllegalGameRoomStateException(message);
        }

        /**
         * 새로운 게임 세션을 생성해 저장
         */
        GameSession gameSession = gameSessionRepository.save(GameSession.createNewGameSession(gameRoom.getId(), command.turnCount(), command.timePerTurn()));
        gameSessionRepository.save(gameSession);

        /**
         * 게임 방 상태를 변경 후 저장
         */
        gameRoom.changeStateToPlay();
        gameRoomRepository.save(gameRoom);

        /**
         * 입장 정보의 state를 PLAYING으로 변경하고, GamePlayHistory entity로 매핑
         * 유저 상태 변경
         * startGame 처리중 exit하는 유저와의 동시성 문제를 막고자 lock을 걸어 조회한다.
         *
         * 250531 게임 시작시 레디상태 false로 모두 변경
         */
        List<GamePlayHistory> gamePlayHistories = gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdWithLock(gameRoom.getId())
                .stream()
                .map(entrance -> {
                    userService.moveUserToGameSession(entrance.getUserId());

                    entrance.resetReady();
                    gameRoomEntranceRepository.save(entrance);

                    return new GamePlayHistory(entrance.getUserId(), gameSession.getId());
                })
                .toList();
        gamePlayHistoryRepository.saveAll(gamePlayHistories);

        /**
         * 이벤트 발행하여 커밋 이후 next turn 및 메세징 처리
         */
        applicationEventPublisher.publishEvent(new GameSessionStartEvent(gameRoom.getId(), gameSession.getId()));

        return gameSession.getId();
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
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        return gameRoomService.isEnteredRoom(gameSession.getGameRoomId(), userId);
    }


    /**
     * 현재 게임 세션을 조회한다.
     *
     * @param gameRoomId
     * @return
     */
    @Transactional(readOnly = true)
    public GameSessionDto findCurrentGameSession(Long gameRoomId) {
        GameSession gameSession = gameSessionRepository.findGameSessionsByGameRoomId(gameRoomId)
                .stream()
                .filter(GameSession::isActive)
                .findFirst()
                .orElseThrow(GameSessionNotFoundException::new);

        gameSessionRepository.findTurnCountByGameSessionId(gameSession.getId());
        return new GameSessionDto(
                gameSession.getId(),
                gameSession.getState(),
                gameSession.getTimePerTurn(),
                (int) gameSessionRepository.findTurnCountByGameSessionId(gameSession.getId()),
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
        GameTurn gameTurn = gameSessionRepository.findLatestTurn(gameSessionId)
                .orElseThrow(GameTurnNotFoundException::new);

        if (!gameTurn.isActive()) {
            log.debug("활성화된 게임 턴이 아닙니다.");
            throw new GameTurnNotFoundException();
        }


        return new GameTurnDto(
                gameTurn.getDrawerId(),
                gameTurn.getDrawerId().equals(userId) ? gameTurn.getAnswer() : null,
                gameService.calculateExpiration(gameTurn));
    }


    /**
     * 다음 턴으로 진행한다.
     * 해당 메소드는 startGame 등과 같은 메소드 이후 시점에 실행되어야 하나
     * 별도의 컨텍스트에서 싫랭되어야 하므로 트랜잭션읇 분리한다.
     */
    @Transactional
    public void processToNextTurn(Long gameSessionId) {
        log.debug("{} 세션 다음 턴으로 진행", gameSessionId);
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        if (gameSession.isEnd()) {
            log.debug("진행중인 게임 세션이 아닙니다. ====== {} : {}", gameSession, gameSession.getState());
            throw new GameSessionIsOverException();
        }

        /**
         * 지난 턴이 아직 ACTIVE로 존재할 경우 pass한다.
         */
        gameSessionRepository.findLatestTurn(
                        gameSession.getId())
                .ifPresent(gameTurn -> {
                    gameTurn.pass();
                    gameSessionRepository.saveGameTurn(gameTurn);
                });

        if (gameService.shouldEnd(gameSession)) {
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
        GameTurn gameTurn = gameSessionRepository.saveGameTurn(
                        GameTurn.createNewGameTurn(
                                gameSessionId,
                                gameService.findNextDrawerId(gameSessionId),
                                dictionaryService.drawRandomWordValue(Language.KOREAN, PartOfSpeech.NOUN)
                        )
                );

        /**
         * 새 턴이 진행되었음을 알리는 event pub
         */
        applicationEventPublisher.publishEvent(
                new GameTurnProgressedEvent(
                        gameSession.getGameRoomId(),
                        gameSessionId,
                        gameTurn.getId()
                )
        );
    }

    /**
     * 게임을 종료한다.
     *
     * @param gameSessionId 대상 게임 세션 ID
     */
    @Transactional
    public void endGame(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        if (gameSession.isEnd()) {
            log.debug("이미 게임 세션이 종료되었습니다.");
            throw new GameSessionIsOverException();
        }
        log.debug("게임 세션 종료 ====== gameSessionId : {}", gameSessionId);


        /**
         * 내부에서만 요청되는 메소드로 클라이언트 validation 처리 없이 complete한다.
         * TODO user 상태, 게임 방 입장 상태 update 처리 추가
         */
        gameRoomService.findGameRoomEntrancesByGameRoomIdAndState(gameSession.getGameRoomId(), GameRoomEntranceState.PLAYING)
                .forEach(gameRoomEntrance -> {
                    gameRoomEntrance.resetGameRoomEntranceInfo();
                    gameRoomEntranceRepository.save(gameRoomEntrance);

                    userService.moveUserToRoom(gameRoomEntrance.getUserId());
                });

        /**
         * 게임 세션 종료 처리
         */
        gameSession.end();
        gameSessionRepository.save(gameSession);

        /**
         * 게임 방 정상 상태로 리셋
         */
        GameRoom gameRoom = gameRoomService.findGameRoom(gameSession.getGameRoomId());
        gameRoom.resetGameRoomState();
        gameRoomRepository.save(gameRoom);


        applicationEventPublisher.publishEvent(new GameSessionEndEvent(gameSessionId, gameSession.getGameRoomId()));
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
        GameSession gameSession = gameSessionRepository.findById(gameSessionId).orElseThrow(GameSessionNotFoundException::new);

        if (gameSession.isPlaying()) {
            log.debug("이미 게임이 진행중입니다.");
            return;
        }
        if (gameSession.isEnd()) {
            log.debug("이미 게임이 종료되었습니다.");
            return;
        }


        int enteredUserCount = gameRoomEntranceRepository.findEnteredUserCount(gameSession.getGameRoomId());

        gameSessionLoadRegistry.register(gameSessionId, userId);

        if (gameSessionLoadRegistry.isAllLoaded(gameSessionId, enteredUserCount)) {
            log.debug("여기는 한 번만");
            gameSession.start();
            gameSessionRepository.save(gameSession);

            gameRoomEntranceRepository
                    .findGameRoomEntrancesByGameRoomIdAndState(gameSession.getGameRoomId(), GameRoomEntranceState.WAITING)
                    .forEach(entrance -> {
                        entrance.changeToPlaying();
                        gameRoomEntranceRepository.save(entrance);
                    });

            applicationEventPublisher.publishEvent(new AllUserInGameSessionLoadedEvent(gameSessionId, gameSession.getGameRoomId()));
            gameSessionLoadRegistry.clear(gameSessionId);
        }
    }
}
