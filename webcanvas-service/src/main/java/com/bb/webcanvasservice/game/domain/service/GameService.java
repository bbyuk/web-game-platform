package com.bb.webcanvasservice.game.domain.service;

import com.bb.webcanvasservice.game.domain.exception.GameSessionIsOverException;
import com.bb.webcanvasservice.game.domain.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.NextDrawerNotFoundException;
import com.bb.webcanvasservice.game.domain.model.GameRoomEntrance;
import com.bb.webcanvasservice.game.domain.model.GameRoomEntranceState;
import com.bb.webcanvasservice.game.domain.model.GameSession;
import com.bb.webcanvasservice.game.domain.model.GameTurn;
import com.bb.webcanvasservice.game.domain.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.game.domain.repository.GameSessionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/**
 * 게임 세션 및 플레이 관련 domain layer service
 */
public class GameService {

    private final GameSessionRepository gameSessionRepository;
    private final GameRoomEntranceRepository gameRoomEntranceRepository;

    public GameService(GameSessionRepository gameSessionRepository, GameRoomEntranceRepository gameRoomEntranceRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.gameRoomEntranceRepository = gameRoomEntranceRepository;
    }

    /**
     * 게임 턴이 모두 진행되어 게임을 종료상태로 변경해야하는지 체크한다.
     *
     * @return 게임을 종료해야되는지 여부
     */
    public boolean shouldEnd(GameSession gameSession) {
        List<GameTurn> turnsInSession = gameSessionRepository.findTurnsByGameSessionId(gameSession.getId());
        return turnsInSession.size() >= gameSession.getTurnCount();
    }

    /**
     * 종료 시간을 계산해 리턴한다.
     * Seconds
     * @return 게임 턴의 만료 시간
     */
    public LocalDateTime calculateExpiration(GameTurn gameTurn) {
        GameSession gameSession = gameSessionRepository.findById(gameTurn.getGameSessionId()).orElseThrow(GameSessionNotFoundException::new);
        return gameTurn.getStartedAt().plusSeconds(gameSession.getTimePerTurn());
    }

    public Long findNextDrawerId(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        if (!gameSession.isPlaying()) {
            throw new GameSessionIsOverException();
        }

        List<GameTurn> gameTurns = gameSessionRepository.findTurnsByGameSessionId(gameSessionId);
        if (gameSession.getTurnCount() <= gameTurns.size()) {
            throw new GameSessionIsOverException();
        }

        /**
         * 현재 게임중인 유저 목록
         */
        List<GameRoomEntrance> gameRoomEntrances = gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdAndState(gameSession.getGameRoomId(), GameRoomEntranceState.PLAYING);

        // 유저별 턴 수 집계
        Map<Long, Integer> drawerCountMap = gameTurns.stream()
                .collect(Collectors.toMap(
                        GameTurn::getDrawerId,
                        gt -> 1,
                        Integer::sum
                ));

        int minCount = Integer.MAX_VALUE;
        List<Long> candidates = new ArrayList<>();

        for (GameRoomEntrance entrance : gameRoomEntrances) {
            Long userId = entrance.getUserId();
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
