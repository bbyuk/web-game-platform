package com.bb.webcanvasservice.game.application.service;

import com.bb.webcanvasservice.game.application.command.StartGameCommand;
import com.bb.webcanvasservice.game.application.dto.GameSessionDto;
import com.bb.webcanvasservice.game.application.dto.GameTurnDto;
import com.bb.webcanvasservice.game.application.port.dictionary.DictionaryQueryPort;
import com.bb.webcanvasservice.game.application.port.user.UserCommandPort;
import com.bb.webcanvasservice.game.application.registry.GameSessionLoadRegistry;
import com.bb.webcanvasservice.game.application.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.game.application.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.application.repository.GameSessionRepository;
import com.bb.webcanvasservice.game.domain.event.AllUserInGameSessionLoadedEvent;
import com.bb.webcanvasservice.game.domain.event.GameTurnProgressedEvent;
import com.bb.webcanvasservice.game.domain.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.GameSessionIsOverException;
import com.bb.webcanvasservice.game.domain.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.NextDrawerNotFoundException;
import com.bb.webcanvasservice.game.domain.model.GamePlayHistory;
import com.bb.webcanvasservice.game.domain.model.gameroom.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/**
 * 게임 세션 및 플레이 관련 application layer service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    /**
     * 도메인 서비스
     */
    private final GameRoomService gameRoomService;
    /**
     * 도메인 레포지토리
     */
    private final GameSessionRepository gameSessionRepository;
    private final GamePlayHistoryRepository gamePlayHistoryRepository;
    private final GameRoomEntranceRepository gameRoomEntranceRepository;
    private final GameRoomRepository gameRoomRepository;

    /**
     * 크로스도메인 포트
     */
    private final UserCommandPort userCommandPort;
    private final DictionaryQueryPort dictionaryQueryPort;

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
        GameRoom gameRoom = gameRoomRepository.findGameRoomById(command.gameRoomId()).orElseThrow(GameRoomNotFoundException::new);

        /**
         * 요청 보낸 유저가 대상 게임 방의 HOST가 맞는지 확인
         */
        gameRoom.validateIsHost(command.userId());

        /**
         * 게임 시작을 위한 게임 방 상태 검증
         */
        gameRoom.validateStateToPlay();


        /**
         * 새로운 게임 세션을 생성해 로드한다.
         */
        gameRoom.loadGameSession(command.timePerTurn());

        Set<GameRoomParticipant> participants = gameRoom.getCurrentParticipants();
        userCommandPort.moveUsersToGameSession(participants.stream().map(GameRoomParticipant::getUserId).collect(Collectors.toList()));

        List<GamePlayHistory> gamePlayHistories = participants
                .stream()
                .map(entrance -> new GamePlayHistory(entrance.getUserId(), gameRoom.getCurrentGameSession().getId()))
                .toList();
        gamePlayHistoryRepository.saveAll(gamePlayHistories);

        /**
         * 이벤트 발행하여 커밋 이후 next turn 및 메세징 처리
         */
        gameRoom.processEventQueue(applicationEventPublisher::publishEvent);

        return gameRoom.getCurrentGameSession().getId();
    }


    /**
     * 현재 게임 세션을 조회한다.
     *
     * @param gameRoomId
     * @return
     */
    @Transactional(readOnly = true)
    public GameSessionDto findCurrentGameSession(Long gameRoomId) {
        GameRoom gameRoom = gameRoomRepository.findGameRoomByGameRoomParticipantId(gameRoomId).orElseThrow(GameRoomNotFoundException::new);
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
    public void processToNextTurn(Long gameSessionId) {
        log.debug("{} 세션 다음 턴으로 진행", gameSessionId);
        GameRoom gameRoom = gameRoomRepository.findGameRoomByGameSessionId(gameSessionId).orElseThrow(GameRoomNotFoundException::new);
        GameSession gameSession = gameRoom.getCurrentGameSession();

        gameSession.passCurrentTurn();

        if (gameSession.shouldEnd()) {
            log.debug("모든 턴이 진행되었습니다.");
            log.debug("게임 세션 종료");

            gameRoom.endCurrentGameSession();

            userCommandPort.moveUsersToRoom(gameRoom.getCurrentParticipants().stream().map(GameRoomParticipant::getUserId).collect(Collectors.toList()));
            return;
        }


        GameTurn gameTurn = gameSession.createNewGameTurn(
                gameRoom.findNextDrawerId(),
                dictionaryQueryPort.drawRandomKoreanNoun()
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
     * 게임 세션 토픽 구독에 성공했다고 상태에 기록하고,
     * 게임 세션 내의 유저들이 모두 로딩 상태가 되면 턴타이머 시작 이벤트를 발행
     *
     * @param userId
     */
    @Transactional
    public void successSubscription(Long gameSessionId, Long userId) {
        GameRoom gameRoom = gameRoomRepository.findGameRoomByGameSessionId(gameSessionId).orElseThrow();
        int enteredUserCount = gameRoom.getCurrentParticipants().size();

        gameSessionLoadRegistry.register(gameSessionId, userId);
        if (gameSessionLoadRegistry.isAllLoaded(gameSessionId, enteredUserCount)) {
            gameRoom.startGameSession();

            gameSessionLoadRegistry.clear(gameSessionId);
        }
    }
}
