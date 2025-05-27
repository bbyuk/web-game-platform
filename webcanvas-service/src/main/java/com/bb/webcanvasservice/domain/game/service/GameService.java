package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.domain.game.dto.request.GameStartRequest;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceInfoResponse;
import com.bb.webcanvasservice.domain.game.entity.*;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.GameSessionIsOverException;
import com.bb.webcanvasservice.domain.game.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.domain.game.exception.NextDrawerNotFoundException;
import com.bb.webcanvasservice.domain.game.repository.GamePlayHistoryRepository;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import static com.bb.webcanvasservice.domain.game.enums.GameSessionState.PLAYING;

/**
 * 게임 세션 및 플레이 관련 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRoomFacade gameRoomService;

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
     * 다음 차례로 그림을 그릴 유저 ID를 찾는다.
     *
     * @param gameSessionId
     * @return
     */
    @Transactional(readOnly = true)
    public Long findNextDrawer(Long gameSessionId) {
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
        List<GameRoomEntrance> gameRoomEntrances = gameRoomService.findGameRoomEntrancesByGameRoomIdAndState(gameSession.getGameRoom().getId(), GameRoomEntranceState.PLAYING);

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

}
