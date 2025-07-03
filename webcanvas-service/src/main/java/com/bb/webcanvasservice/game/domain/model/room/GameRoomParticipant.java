package com.bb.webcanvasservice.game.domain.model.room;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.game.domain.exception.GameRoomHostCanNotChangeReadyException;

import java.time.LocalDateTime;
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
    private Long gameRoomId;

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
     * 입장 시각
     */
    private LocalDateTime joinedAt;

    /**
     * 퇴장 시각
     */
    private LocalDateTime exitAt;

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getExitAt() {
        return exitAt;
    }

    /**
     * 새로운 입장자 도메인 객체를 생성해 리턴한다.
     * @param userId 입장하는 유저 ID
     * @param nicknameAdjective 닉네임의 앞에 붙을 형용사
     * @return 입장자
     */
    public static GameRoomParticipant create(Long userId, String nicknameAdjective) {
        return new GameRoomParticipant(
                null,
                null,
                userId,
                GameRoomParticipantState.INIT,
                String.format("%s %s", nicknameAdjective, "플레이어"),
                GameRoomParticipantRole.GUEST,
                false,
                null,
                null
        );
    }
    
    public GameRoomParticipant(
            Long id,
            Long gameRoomId,
            Long userId,
            GameRoomParticipantState state,
            String nickname,
            GameRoomParticipantRole role,
            boolean ready,
            LocalDateTime joinedAt,
            LocalDateTime exitAt) {
        this.id = id;
        this.gameRoomId = gameRoomId;
        this.userId = userId;
        this.state = state;
        this.nickname = nickname;
        this.role = role;
        this.ready = ready;
        this.joinedAt = joinedAt;
        this.exitAt = exitAt;
    }

    /**
     * 게임 입장자를 exit 처리한다.
     */
    public void exit() {
        this.state = GameRoomParticipantState.EXITED;
        this.exitAt = LocalDateTime.now();
    }

    /**
     * 게임 입장자를 입장 시킨다.
     */
    public void join(Long gameRoomId) {
        this.gameRoomId = gameRoomId;
        this.state = GameRoomParticipantState.WAITING;
        this.joinedAt = LocalDateTime.now();
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
    void changeReady(boolean ready) {
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
     * 게임 방 입장자 상태를 대기중으로 변경한다.
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

    /**
     * 게임 방 입장자 객체가 활성상태인지 체크한다.
     * @return 활성 상태 여부
     */
    public boolean isActive() {
        return GameRoomParticipantState.joined.contains(state);
    }


    /**
     * 유저가 호스트인지 체크한다.
     * @return
     */
    public boolean isHost() {
        return this.role == GameRoomParticipantRole.HOST;
    }
    /**
     * 게임 방 입장자가 레디를 했는지 여부를 리턴한다.
     * 호스트라면 무조건 true를 리턴한다.
     * @return 레디 여부
     */
    public boolean isReady() {
        if (this.role == GameRoomParticipantRole.HOST) {
            return true;
        }

        return ready;
    }

    /**
     * 게임 방 입장자의 상태가 PLAYING인지 여부를 리턴한다.
     * @return 플레잉 여부
     */
    public boolean isPlaying() {
        return this.state == GameRoomParticipantState.PLAYING;
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
