package com.bb.webcanvasservice.game.domain.model.room;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.domain.shared.AggregateRoot;
import com.bb.webcanvasservice.domain.shared.event.ApplicationEvent;
import com.bb.webcanvasservice.game.domain.event.GameRoomExitEvent;
import com.bb.webcanvasservice.game.domain.event.GameRoomHostChangedEvent;
import com.bb.webcanvasservice.game.domain.event.UserReadyChanged;
import com.bb.webcanvasservice.game.domain.exception.GameRoomParticipantNotFoundException;
import com.bb.webcanvasservice.game.domain.exception.IllegalGameRoomStateException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 게임 방을 나타내는 도메인 모델
 */
public class GameRoom extends AggregateRoot {

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
     * 게임 방의 정원
     */
    private int capacity;


    /**
     * 현재 게임 방의 입장자 목록
     */
    private List<GameRoomParticipant> participants;

    public GameRoom(Long id, String joinCode, GameRoomState state, int capacity, List<GameRoomParticipant> participants) {
        this.id = id;
        this.joinCode = joinCode;
        this.state = state;
        this.capacity = capacity;
        this.participants = participants;
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

    public int getCapacity() {
        return capacity;
    }

    public List<GameRoomParticipant> getParticipants() {
        return participants;
    }

    /**
     * 새 게임 방을 생성해 리턴한다.
     *
     * @return 게임 방
     */
    public static GameRoom create(String joinCode, int capacity) {
        return new GameRoom(null, joinCode, GameRoomState.WAITING, capacity, new ArrayList<>());
    }


    public boolean isWaiting() {
        return this.state == GameRoomState.WAITING;
    }

    /**
     * 남은 게임 방 입장 유저들을 모두 내보내고 상태를 close한다.
     */
    public void close() {
        getCurrentParticipants().stream().forEach(GameRoomParticipant::exit);
        this.state = GameRoomState.CLOSED;
    }

    /**
     * 새로운 입장자를 입장시킨다.
     *
     * @param newParticipant
     */
    public void letIn(GameRoomParticipant newParticipant) {
        checkCanJoin();

        if (participants.isEmpty()) {
            newParticipant.changeRoleToHost();
        }

        newParticipant.join(id);
        participants.add(newParticipant);
    }

    /**
     * 대상 GameRoomParticipant를 내보내다.
     *
     * @param targetParticipant 대상 participant
     */
    public void sendOut(GameRoomParticipant targetParticipant) {
        targetParticipant.exit();
        eventQueue.add(new GameRoomExitEvent(id, targetParticipant.getUserId()));

        List<GameRoomParticipant> currentParticipants = getCurrentParticipants();

        if (currentParticipants.isEmpty()) {
            this.close();
        } else if (targetParticipant.isHost()) {
            /**
             * 250522
             * 퇴장 요청을 보낸 유저가 HOST일 경우 남은 유저 중 제일 처음 입장한 유저가 HOST가 된다.
             */
            currentParticipants.stream()
                    .sorted(Comparator.comparing(GameRoomParticipant::getJoinedAt))
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
     *
     * @return 게임 방 close 여부
     */
    public boolean isClosed() {
        return this.state == GameRoomState.CLOSED;
    }

    /**
     * 현재 퇴장하지 않은 게임 방 입장자 목록 조회
     *
     * @return
     */
    public List<GameRoomParticipant> getCurrentParticipants() {
        return participants
                .stream()
                .filter(GameRoomParticipant::isActive)
                .collect(Collectors.toList());
    }

    /**
     * 게임 방 입장자 ID로 현재 게임방에 접속한 입장자 객체 조회
     *
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
     * 대상 게임방 입장자의 레디 상태를 변경한다.
     *
     * @param targetParticipant 게임방 입장자
     * @param ready             변경할 레디 상태
     */
    public void changeParticipantReady(GameRoomParticipant targetParticipant, boolean ready) {
        targetParticipant.changeReady(ready);
        eventQueue.add(new UserReadyChanged(id, targetParticipant.getUserId(), ready));
    }

    /**
     * 대상 유저가 호스트인지 확인한다.
     *
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
    public void validateStateToLoad() {
        if (!isWaiting()) {
            throw new IllegalGameRoomStateException("게임 방의 상태가 게임을 시작할 수 없는 상태입니다.");
        }

        List<GameRoomParticipant> currentParticipants = getCurrentParticipants();
        /**
         * 모든 유저가 레디 상태여야 한다.
         */
        if (currentParticipants
                .stream()
                .filter(GameRoomParticipant::isReady)
                .count() < currentParticipants.size()) {
            throw new IllegalGameRoomStateException("레디를 하지 않은 유저가 있습니다.");
        }

        /**
         * 게임을 시작하기 위해서는 방에 인원은 최소 2명 이상이어야 한다.
         */
        if (currentParticipants.size() < 2) {
            throw new IllegalGameRoomStateException("게임을 시작하기 위해서는 최소 2명 이상이 필요합니다.");
        }
    }

    /**
     * 새 게임 세션을 생성해 loading 상태로 할당한다.
     */
    public void changeStateToPlay() {
        this.state = GameRoomState.PLAYING;

        List<GameRoomParticipant> currentParticipants = getCurrentParticipants();

        /**
         * 입장자 state를 PLAYING으로 변경
         * 250531 게임 시작시 레디상태 false로 모두 변경
         */
        currentParticipants.forEach(
                gameRoomParticipant -> {
                    gameRoomParticipant.resetReady();
                    gameRoomParticipant.changeStateToPlaying();
                }
        );
    }

    /**
     * 현재 게임 세션을 종료한다.
     */
    public void resetToWaiting() {
        List<GameRoomParticipant> currentParticipants = getCurrentParticipants();
        currentParticipants.forEach(GameRoomParticipant::changeStateToWaiting);

        /**
         * 게임 방 초기 상태로 리셋
         */
        this.state = GameRoomState.WAITING;
    }

    /**
     * 유저 ID로 게임 방 입장자를 찾아 리턴한다.
     *
     * @param targetUserId 대상 유저 ID
     * @return 게임 방 입장자 객체
     */
    public GameRoomParticipant findParticipantByUserId(Long targetUserId) {
        return getCurrentParticipants()
                .stream()
                .filter(participant -> participant.getUserId().equals(targetUserId))
                .findFirst()
                .orElseThrow(GameRoomParticipantNotFoundException::new);
    }

    /**
     * ===================== 내부 상태 쿼리 메소드 =================
     */

    /**
     * 게임 방에 입장할 수 있는 상태인지 확인한다.
     */
    private void checkCanJoin() {
        if (!isWaiting()) {
            throw new IllegalGameRoomStateException();
        }

        int enteredUserCounts = getCurrentParticipants().size();

        if (enteredUserCounts >= capacity) {
            throw new IllegalGameRoomStateException("방의 정원이 모두 찼습니다.");
        }
    }

    /**
     * 현재 게임 방 입장자 중 유저 ID에 해당하는 대상 입장자 객체를 리턴한다.
     * @param userId 유저 ID
     * @return 게임 방 입장자 객체
     */
    public GameRoomParticipant getCurrentParticipantByUserId(Long userId) {
        return getCurrentParticipants().stream()
                .filter(gameRoomParticipant -> gameRoomParticipant.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(GameRoomParticipantNotFoundException::new);
    }


}
