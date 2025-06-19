package com.bb.webcanvasservice.game.domain.model.gameroom;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.game.domain.exception.GameRoomHostCanNotChangeReadyException;

import java.util.Objects;

/**
 * 게임 방 입장 유저를 나타내는 도메인 모델
 */
public class GameRoomParticipant {

    /**
     * 게임 방 입장 유저 ID
     */
    private final Long id;

    /**
     * 입장한 게임 방
     */
    private final Long gameRoomId;

    /**
     * 입장한 유저 ID
     */
    private final Long userId;

    /**
     * 게임 방 내에서의 유저 닉네임 -> 사전에서 랜덤으로 찾아와 조합할 예정
     */
    private String nickname;

    /**
     * 게임 방 내에서의 역할
     */
    private GameRoomParticipantRole role;

    /**
     * 게임 방 입장 기록 상태
     */
    private GameRoomParticipantState state;

    /**
     * 준비 여부
     */
    private boolean ready;

    /**
     * 새로운 입장자 도메인 객체를 생성해 리턴한다.
     * @param gameRoomId 입장하는 게임 방 ID
     * @param userId 입장하는 유저 ID
     * @param nicknameAdjective 닉네임의 앞에 붙을 형용사
     * @param role 입장자 role
     * @return 입장자
     */
    public static GameRoomParticipant create(Long gameRoomId, Long userId, String nicknameAdjective) {
        return new GameRoomParticipant(
                null,
                gameRoomId,
                userId,
                GameRoomParticipantState.WAITING,
                String.format("%s %s", nicknameAdjective, "플레이어"),
                GameRoomParticipantRole.GUEST,
                false
        );
    }

    public boolean isReady() {
        if (this.role == GameRoomParticipantRole.HOST) {
            return true;
        }

        return ready;
    }

    public GameRoomParticipant(Long id, Long gameRoomId, Long userId, GameRoomParticipantState state, String nickname, GameRoomParticipantRole role, boolean ready) {
        this.id = id;
        this.gameRoomId = gameRoomId;
        this.userId = userId;
        this.state = state;
        this.nickname = nickname;
        this.role = role;
        this.ready = ready;
    }

    /**
     * 게임 입장 Entity를 exit 처리한다.
     */
    public void exit() {
        this.state = GameRoomParticipantState.EXITED;
    }

    /**
     * 역할을 변경한다.
     * @param gameRoomParticipantRole 게임 방 입장 역할
     */
    public void changeRole(GameRoomParticipantRole gameRoomParticipantRole) {
        this.role = gameRoomParticipantRole;
        if (this.role == GameRoomParticipantRole.HOST) {
            this.ready = true;
        }
    }

    /**
     * 레디 상태를 바꾼다.
     */
    public void changeReady(boolean ready) {
        if (isHost()) {
            throw new GameRoomHostCanNotChangeReadyException();
        }
        this.ready = ready;
    }


    /**
     * 게임 방 입장자 상태를 게임 진행중으로 변경한다.
     */
    public void changeStateToPlaying() {
        this.state = GameRoomParticipantState.PLAYING;
    }

    /**
     * 게임 방 상태를 대기중으로 변경한다.
     */
    public void changeStateToWaiting() {
        this.state = GameRoomParticipantState.WAITING;
    }

    /**
     * 레디를 초기화한다.
     */
    public void resetReady() {
        this.ready = GameRoomParticipantRole.HOST == this.role;
    }

    /**
     * 유저가 호스트인지 체크한다.
     * @return
     */
    public boolean isHost() {
        return this.role == GameRoomParticipantRole.HOST;
    }

    public Long getId() {
        return id;
    }

    public Long getGameRoomId() {
        return gameRoomId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public GameRoomParticipantState getState() {
        return state;
    }

    public GameRoomParticipantRole getRole() {
        return role;
    }

    /**
     * 게임 방 입장자는 자체 애그리거트로 id 로만 비교
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameRoomParticipant that = (GameRoomParticipant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 유저 ID를 validate 한다
     * @param targetUserId
     */
    public void validate(Long targetUserId) {
        if (!userId.equals(targetUserId)) {
            throw new AbnormalAccessException();
        }
    }

    public boolean isActive() {
        return this.state == GameRoomParticipantState.PLAYING
                || this.state == GameRoomParticipantState.WAITING;
    }

    /**
     * 게임 방 입장자 역할을 HOST로 변경한다.
     * HOST는 항상 ready true이다.
     */
    public void changeRoleToHost() {
        this.role = GameRoomParticipantRole.HOST;
        this.ready = true;
    }
}
