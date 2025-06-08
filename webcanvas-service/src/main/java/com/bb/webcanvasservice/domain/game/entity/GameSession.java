package com.bb.webcanvasservice.domain.game.entity;

import com.bb.webcanvasservice.common.entity.BaseEntity;
import com.bb.webcanvasservice.domain.game.enums.GameSessionState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 게임의 세션을 나타내는 엔티티 클래스
 */
@Entity
@Getter
@Table(name = "game_sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSession extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 게임 세션 ID
     */
    private Long id;

    @Column(name = "turn_count")
    /**
     * 게임 턴 수
     */
    private int turnCount;

    @Column(name = "time_per_turn")
    /**
     * 턴 당 시간
     */
    private int timePerTurn;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    /**
     * 게임 세션의 상태
     */
    private GameSessionState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room_id")
    /**
     * 게임 방
     */
    private GameRoom gameRoom;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gameSession")
    /**
     * 세션에 포함되어 있는 턴 목록
     */
    private List<GameTurn> gameTurns = new ArrayList<>();

    public GameSession(GameRoom gameRoom, int turnCount, int timePerTurn) {
        this.gameRoom = gameRoom;
        this.state = GameSessionState.LOADING;
        this.turnCount = turnCount;
        this.timePerTurn = timePerTurn;
    }

    /**
     * 게임 세션을 종료하고 게임 세션과 연관되어 있는 게임방 객체의 상태 리셋을 요청한다.
     */
    public void end() {
        state = GameSessionState.COMPLETED;
        gameRoom.resetGameRoomState();
    }

    /**
     * 게임 세션을 시작한다.
     */
    public void start() {
        state = GameSessionState.PLAYING;
    }

    public boolean isPlaying() {
        return state == GameSessionState.PLAYING;
    }

    /**
     * 게임 종료인지 여부를 체크한다.
     * @return 게임 종료 여부
     */
    public boolean isEnd() {
        return state == GameSessionState.COMPLETED;
    }

    /**
     * 게임 턴이 모두 진행되어 게임을 종료상태로 변경해야하는지 체크한다.
     * @return 게임을 종료해야되는지 여부
     */
    public boolean shouldEnd() {
        return gameTurns.size() >= turnCount;
    }

    /**
     * 지난 턴 조회
     * @return 지난 턴
     */
    public Optional<GameTurn> getLastTurn() {
        return gameTurns
                .stream()
                .filter(GameTurn::isActive)
                .sorted(Comparator.comparingLong(GameTurn::getId))
                .findFirst();
    }

}
