package com.bb.webcanvasservice.game.domain.model.gameroom;

import com.bb.webcanvasservice.common.event.ApplicationEvent;
import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.game.domain.event.*;
import com.bb.webcanvasservice.game.domain.exception.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/**
 * 게임 방을 나타내는 도메인 모델
 */
public class GameRoom {

    private final List<ApplicationEvent> eventQueue = new ArrayList<>();

    /**
     * 게임 방 ID
     */
    private final Long id;


    /**
     * 게임 방의 입장 코드
     */
    private final String joinCode;

    /**
     * 게임 방 상태
     */
    private GameRoomState state;

    /**
     * 현재 게임 방에서 진행중인 게임 세션
     */
    private GameSession currentGameSession;

    /**
     * 현재 게임 방의 입장자 목록
     */
    private Set<GameRoomParticipant> participants;

    /**
     * 게임 방의 정원
     */
    private int gameRoomCapacity;

    public GameRoom(Long id, String joinCode, GameRoomState state) {
        this.id = id;
        this.joinCode = joinCode;
        this.state = state;
    }

    /**
     * 새 게임 방을 생성해 리턴한다.
     *
     * @return 게임 방
     */
    public static GameRoom create(String joinCode) {
        return new GameRoom(null, joinCode, GameRoomState.WAITING);
    }

    /**
     * 게임 방을 플레이 상태로 변경한다.
     */
    public void changeStateToPlay() {
        this.state = GameRoomState.PLAYING;
    }

    /**
     * 게임 세션이 종료된 후 WAITING 상태로 방 상태를 리셋한다.
     */
    public void resetGameRoomState() {
        this.state = GameRoomState.WAITING;
    }

    public boolean isWaiting() {
        return this.state == GameRoomState.WAITING;
    }

    /**
     * 게임 방 상태를 close한다.
     */
    public void close() {
        this.state = GameRoomState.CLOSED;
    }

    /**
     * 현재 게임 세션을 가져온다.
     * @return
     */
    public GameSession getCurrentGameSession() {
        if (currentGameSession == null) {
            throw new GameSessionNotFoundException();
        }

        if (!currentGameSession.isPlaying()) {
            throw new GameSessionIsOverException();
        }

        return currentGameSession;
    }

    public Long getId() {
        return id;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public GameRoomState getState() {
        return state;
    }

    /**
     * 게임 방에 입장할 수 있는 상태인지 확인한다.
     */
    public void checkCanJoin() {
        if (isWaiting()) {
            throw new IllegalGameRoomStateException();
        }

        int enteredUserCounts = getCurrentParticipants().size();

        if (enteredUserCounts >= gameRoomCapacity) {
            throw new IllegalGameRoomStateException("방의 정원이 모두 찼습니다.");
        }
    }

    /**
     * 대상 GameRoomParticipant를 내보내다.
     *
     * @param targetParticipant 대상 participant
     */
    public void sendOut(GameRoomParticipant targetParticipant) {
        targetParticipant.exit();
        eventQueue.add(new GameRoomExitEvent(id, targetParticipant.getUserId()));

        Set<GameRoomParticipant> currentParticipants = getCurrentParticipants();

        if (currentParticipants.isEmpty()) {
            this.close();
        } else if (targetParticipant.isHost()) {
            /**
             * 250522
             * 퇴장 요청을 보낸 유저가 HOST일 경우 남은 유저 중 제일 처음 입장한 유저가 HOST가 된다.
             */
            currentParticipants.stream()
                    .filter(participant -> !participant.equals(targetParticipant))
                    .sorted(Comparator.comparingLong(GameRoomParticipant::getId))
                    .findFirst()
                    .ifPresentOrElse(
                            participant -> {
                                participant.changeRoleToHost();
                                eventQueue.add(new GameRoomHostChangedEvent(id, participant.getUserId()));
                            },
                            this::close
                    );
        }
    }

    /**
     * 게임 방이 닫힌지 확인한다.
     * @return 게임 방 close 여부
     */
    public boolean isClosed() {
        return this.state == GameRoomState.CLOSED;
    }

    /**
     * 현재 퇴장하지 않은 게임 방 입장자 목록 조회
     * @return
     */
    public Set<GameRoomParticipant> getCurrentParticipants() {
        return participants
                .stream()
                .filter(GameRoomParticipant::isActive)
                .collect(Collectors.toSet());
    }

    /**
     * 게임 방 입장자 ID로 현재 게임방에 접속한 입장자 객체 조회
     * @param targetRoomParticipantId 대상 게임 방 입장자 ID
     * @return 게임 방 입장자
     */
    public GameRoomParticipant findParticipant(Long targetRoomParticipantId) {
        return participants
                .stream()
                .filter(participant ->
                        participant.getId().equals(targetRoomParticipantId) && participant.isActive()
                )
                .findFirst()
                .orElseThrow(GameRoomParticipantNotFoundException::new);
    }

    /**
     * 게임 방 이벤트 큐에 들어있는 이벤트를 순차적으로 모두 처리하고 클리어한다.
     * @param eventsPublisher
     */
    public void processEventQueue(Consumer<ApplicationEvent> eventsPublisher) {
        eventQueue.forEach(eventsPublisher::accept);
        eventQueue.clear();
    }

    /**
     * 대상 게임방 입장자의 레디 상태를 변경한다.
     * @param targetParticipantId 게임방 입장자 ID
     * @param ready 변경할 레디 상태
     */
    public void changeParticipantReady(Long targetParticipantId, boolean ready) {
        GameRoomParticipant targetParticipant = findParticipant(targetParticipantId);
        targetParticipant.changeReady(ready);

        eventQueue.add(new UserReadyChanged(targetParticipant.getGameRoomId(), targetParticipant.getUserId(), ready));
    }

    /**
     * 대상 유저가 호스트인지 확인한다.
     * @param userId
     */
    public void validateIsHost(Long userId) {
        GameRoomParticipant targetParticipant = getCurrentParticipants()
                .stream()
                .filter(participant -> participant.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(GameRoomParticipantNotFoundException::new);

        if (!targetParticipant.isHost()) {
            throw new AbnormalAccessException();
        }
    }

    /**
     * 게임 방 상태가 시작할 수 있는 상태인지 검증한다.
     */
    public void validateStateToPlay() {
        if (!isWaiting()) {
            throw new IllegalGameRoomStateException("게임 방의 상태가 게임을 시작할 수 없는 상태입니다.");
        }

        /**
         * 현재 진행중인 게임 세션이 있는지 확인
         */
        if (currentGameSession != null) {
            throw new IllegalGameRoomStateException("이미 게임 세션이 진행중입니다.");
        }
    }

    /**
     * 새 게임 세션을 생성해 loading 상태로 할당한다.
     */
    public void loadGameSession(int timePerTurn) {
        Set<GameRoomParticipant> currentParticipants = getCurrentParticipants();

        int turnCount = currentParticipants.size();
        this.currentGameSession = GameSession.create(id, turnCount, timePerTurn);

        changeStateToPlay();


        /**
         * 입장자 state를 PLAYING으로 변경하고, GamePlayHistory entity로 매핑
         * 유저 상태 변경
         * startGame 처리중 exit하는 유저와의 동시성 문제를 막고자 lock을 걸어 조회한다.
         *
         * 250531 게임 시작시 레디상태 false로 모두 변경
         */
        currentParticipants.forEach(GameRoomParticipant::resetReady);

        eventQueue.add(new GameSessionStartEvent(id, currentGameSession.getId()));
    }

    /**
     * 현재 게임 세션을 종료한다.
     */
    public void endCurrentGameSession() {
        GameSession gameSession = getCurrentGameSession();
        if (gameSession.isEnd()) {
            throw new GameSessionIsOverException();
        }

        Set<GameRoomParticipant> currentParticipants = getCurrentParticipants();
        currentParticipants.forEach(GameRoomParticipant::changeStateToWaiting);

        gameSession.end();

        /**
         * 게임 방 초기 상태로 리셋
         */
        this.resetGameRoomState();

        eventQueue.add(new GameSessionEndEvent(gameSession.getId(), id));
    }

    /**
     * 로드되어 있는 게임 세션을 실행한다.
     */
    public void startGameSession() {
        GameSession gameSession = getCurrentGameSession();
        if (!gameSession.isLoading()) {
            throw new GameSessionNotFoundException();
        }

        gameSession.start();
        getCurrentParticipants()
                .forEach(GameRoomParticipant::changeStateToPlaying);

        eventQueue.add(new AllUserInGameSessionLoadedEvent(gameSession.getId(), id));
    }


    public Long findNextDrawerId() {
        GameSession gameSession = getCurrentGameSession();

        /**
         * 현재 게임중인 유저 목록
         */
        Set<GameRoomParticipant> gameRoomParticipants = getCurrentParticipants();

        // 유저별 턴 수 집계
        Map<Long, Integer> drawerCountMap = gameSession.getDrawerCountMap();

        int minCount = Integer.MAX_VALUE;
        List<Long> candidates = new ArrayList<>();

        for (GameRoomParticipant entrance : gameRoomParticipants) {
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
