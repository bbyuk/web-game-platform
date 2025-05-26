package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.domain.game.dto.request.GameStartRequest;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceInfoResponse;
import com.bb.webcanvasservice.domain.game.entity.GamePlayHistory;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.domain.game.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게임 세션 및 플레이 관련 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRoomService gameRoomService;

    private final GameSessionRepository gameSessionRepository;
    private final GamePlayHistoryRepository gamePlayHistoryRepository;

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

        GameRoom gameRoom = gameRoomService.findGameRoom(request.gameRoomId());
        GameRoomEntranceInfoResponse enteredGameRoomInfo = gameRoomService.findEnteredGameRoomInfo(userId);

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
        List<GamePlayHistory> gamePlayHistories = gameRoomService.findCurrentGameRoomEntrancesWithLock(gameRoom.getId())
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

        return gameSession.getId();
    }

    /**
     * 게임 세션에 포함된 턴 목록을 조회한다.
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
     * 현재 라운드를 조회한다.
     */
    @Transactional(readOnly = true)
    public int findCurrentRound(Long gameSessionId) {
        return gameSessionRepository.findCurrentRound(gameSessionId);
    }
}
