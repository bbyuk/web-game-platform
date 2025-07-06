package com.bb.webcanvasservice.game.domain.model.session;

import com.bb.webcanvasservice.domain.shared.AggregateRoot;
import com.bb.webcanvasservice.game.domain.event.GameTurnProgressRequestedEvent;
import com.bb.webcanvasservice.game.domain.exception.GameSessionIsOverException;
import com.bb.webcanvasservice.game.domain.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.GameTurnNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.NextDrawerNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/**
 * 게임의 세션을 나타내는 도메인 모델
 */
public class GameSession extends AggregateRoot {

    /**
     * 게임 세션 ID
     */
    private final Long id;

    /**
     * 게임 방
     */
    private final Long gameRoomId;

    /**
     * 게임 턴 수
     */
    private final int turnCount;

    /**
     * 턴 당 시간
     */
    private final int timePerTurn;

    /**
     * 게임 세션의 상태
     */
    private GameSessionState state;

    /**
     * 게임 플레이어 목록
     */
    private final List<GamePlayer> gamePlayers;

    /**
     * 현재 세션에 포함된 게임 턴들
     */
    private List<GameTurn> gameTurns;

    // ===================================================
    // ====================== getter =====================
    // ===================================================
    public Long gameRoomId() {
        return gameRoomId;
    }
    public int turnCount() {
        return turnCount;
    }

    public int timePerTurn() {
        return timePerTurn;
    }

    public Long id() {
        return id;
    }


    public GameSessionState state() {
        return state;
    }

    public List<GamePlayer> gamePlayers() {
        return gamePlayers;
    }

    public List<GameTurn> gameTurns() {
        return gameTurns;
    }

    // ===================================================
    // ====================== getter =====================
    // ===================================================


    public GameSession(Long id, Long gameRoomId, int turnCount, int timePerTurn, GameSessionState state, List<GamePlayer> gamePlayers, List<GameTurn> gameTurns) {
        this.id = id;
        this.gameRoomId = gameRoomId;
        this.turnCount = turnCount;
        this.timePerTurn = timePerTurn;
        this.state = state;
        this.gamePlayers = gamePlayers;
        this.gameTurns = gameTurns;
    }

    /**
     * 게임 세션이 새로 생성 된 시점엔 LOADING 상태로 생성된다.
     * @param turnCount 턴 수
     * @param timePerTurn 턴별 시간
     * @return 새로 생성된 게임 세션 객체
     */
    public static GameSession create(Long gameRoomId, int turnCount, int timePerTurn) {
        return new GameSession(null, gameRoomId, turnCount, timePerTurn, GameSessionState.LOADING, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * 새 게임턴을 할당한다.
     * @param answer 새로 할당할 턴의 정답.
     */
    public void allocateNewGameTurn(String answer) {
        GameTurn newGameTurn = GameTurn.create(id, findNextDrawerId(), answer, timePerTurn);
        gameTurns.add(newGameTurn);
    }

    /**
     * 해당 세션을 종료해야하는지 여부를 체크한다.
     * @return 종료 여부
     */
    public boolean shouldEnd() {
        return this.turnCount <= getCompletedGameTurnCount();
    }

    /**
     * 게임 세션이 현재 플레이 중인지 확인한다.
     * @return 게임 세션 현재 플레이 여부
     */
    public boolean isPlaying() {
        return state == GameSessionState.PLAYING;
    }

    /**
     * 게임 세션이 활성상태인지 확인한다.
     * @return 게임 세션 현재 활성 여부
     */
    public boolean isActive() {
        return state == GameSessionState.PLAYING || state == GameSessionState.LOADING;
    }

    /**
     * 게임 종료인지 여부를 체크한다.
     * @return 게임 종료 여부
     */
    public boolean isEnd() {
        return state == GameSessionState.COMPLETED;
    }

    /**
     * 현재 게임 방에서 진행중인 세션의 턴 정답을 체크한다.
     * @param senderId
     * @param value
     */
    public void checkAnswer(Long senderId, String value) {
        GameTurn currentTurn = getCurrentTurn();

        if (currentTurn.isDrawer(senderId)) {
            if (currentTurn.containsAnswer(value)) {
                /**
                 * 해당 턴의 drawer가 정답을 포함한 채팅을 입력할 시 해당턴은 PASS로 처리
                 *
                 * TODO 해당 유저에게 패널티 부여
                 */
                currentTurn.pass();
                eventQueue.add(new GameTurnProgressRequestedEvent(gameRoomId, id, timePerTurn, currentTurn.id()));
            }
        }
        else {
            if (currentTurn.isAnswer(value))  {
                currentTurn.markingAsCorrect(senderId);
                eventQueue.add(new GameTurnProgressRequestedEvent(gameRoomId, id, currentTurn.id(), timePerTurn, senderId));
            }
        }
    }

    /**
     * 게임 방 내에서 다음 그림 그릴 사람을 찾는다.
     *
     * @return 게임 방내에서 다음 그릴 사람 user id 리턴
     */
    public Long findNextDrawerId() {
        /**
         * 현재 게임중인 유저 목록
         */
        // 유저별 턴 수 집계
        Map<Long, Integer> drawerCountMap = gameTurns.stream()
                .collect(Collectors.toMap(
                        GameTurn::drawerId,
                        gt -> 1,
                        Integer::sum
                ));

        int minCount = Integer.MAX_VALUE;
        List<Long> candidates = new ArrayList<>();


        for (GamePlayer gamePlayer : gamePlayers.stream().filter(GamePlayer::isPlaying).toList()) {
            Long userId = gamePlayer.userId();
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


    /**
     * 종료된 게임 턴의 수를 리턴한다.
     * @return 종료된 게임 턴 수
     */
    public int getCompletedGameTurnCount() {
        return (int) gameTurns.stream().filter(GameTurn::isCompleted).count();
    }

    public GameTurn getCurrentTurn() {
        if (gameTurns.isEmpty()) {
            throw new GameTurnNotFoundException();
        }

        GameTurn currentTurn = gameTurns.get(gameTurns.size() - 1);

        if (!currentTurn.isActive()) {
            throw new GameTurnNotFoundException();
        }
        return currentTurn;
    }

    /**
     * 현재 턴을 pass 한다
     */
    public void passCurrentTurn() {
        if (gameTurns.isEmpty()) {
            return;
        }
        GameTurn currentTurn = gameTurns.get(gameTurns.size() - 1);
        if (currentTurn.isCompleted()) {
            return;
        }
        currentTurn.pass();
    }

    /**
     * 세션이 로딩 중인지 여부를 확인한다.
     * @return
     */
    public boolean isLoading() {
        return state == GameSessionState.LOADING;
    }

    /**
     * Game Player들을 모두 등록시킨다.
     * @param gamePlayers game player
     */
    public void involvePlayers(List<GamePlayer> gamePlayers) {
        this.gamePlayers.addAll(gamePlayers);
    }

    /**
     * 게임 세션을 종료하고 게임 세션과 연관되어 있는 게임방 객체의 상태 리셋을 요청한다.
     */
    public void end() {
        if (isEnd()) {
            throw new GameSessionIsOverException();
        }

        this.state = GameSessionState.COMPLETED;
        gamePlayers.forEach(GamePlayer::deactivate);
    }

    /**
     * 로드되어 있는 게임 세션을 실행한다.
     */
    public void start() {
        if (!isLoading()) {
            throw new GameSessionNotFoundException();
        }
        state = GameSessionState.PLAYING;
        gamePlayers.forEach(GamePlayer::changeStateToPlaying);
    }

    /**
     * 모든 플레이어가 세션에 로드되었는지 확인한다.
     * @return 모든 플레이어 세션 로드 여부
     */
    public boolean isAllPlayersLoaded() {
        return gamePlayers.stream().allMatch(GamePlayer::isLoaded);
    }

    /**
     * 플레이어를 세션에 로드한다.
     * @param userId 플레이어의 유저 ID
     */
    public void loadPlayer(Long userId) {
        gamePlayers.stream()
                .filter(gamePlayer -> gamePlayer.userId().equals(userId))
                .findFirst()
                .ifPresent(GamePlayer::load);
    }
}
