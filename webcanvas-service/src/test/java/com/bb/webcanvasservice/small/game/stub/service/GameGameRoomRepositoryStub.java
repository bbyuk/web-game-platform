package com.bb.webcanvasservice.small.game.stub.service;

import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipant;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomState;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameGameRoomRepositoryStub implements GameRoomRepository {

    private long gameRoomSeq = 0L;

    private long gameRoomParticipantSeq = 0L;
    private long gameSessionSEq = 0L;
    private long gameTurnSeq = 0L;

    private Map<Long, GameRoom> gameRooms = new HashMap<>();

    private Map<Long, GameRoomParticipant> gameRoomParticipants = new HashMap<>();

    @Override
    public Optional<GameRoom> findGameRoomById(Long gameRoomId) {
        return Optional.of(gameRooms.get(gameRoomId));
    }

    @Override
    public Optional<GameRoom> findGameRoomByGameRoomParticipantId(Long gameRoomParticipantId) {
        return Optional.empty();
    }

    @Override
    public Optional<GameRoom> findGameRoomByGameSessionId(Long gameSessionId) {
        return Optional.empty();
    }

    @Override
    public Optional<GameRoom> findCurrentJoinedGameRoomByUserId(Long userId) {
        return Optional.empty();
    }

    @Override
    public boolean existsJoinCodeConflictOnActiveGameRoom(String joinCode) {
        return false;
    }

    @Override
    public List<GameRoom> findGameRoomsByCapacityAndStateWithEntranceState(int gameRoomCapacity, List<GameRoomState> enterableStates, GameRoomParticipantState activeEntranceState) {
        return null;
    }

    @Override
    public Optional<GameRoom> findRoomWithJoinCodeForEnter(String joinCode) {
        return Optional.empty();
    }

    @Override
    public GameRoom save(GameRoom gameRoom) {
        try {
            Field idField = GameRoom.class.getDeclaredField("id");
            idField.setAccessible(true);

            /**
             * 게임 방 입장자 저장
             */
            saveAll(gameRoom.getParticipants());
            /**
             * TODO 턴 목록 저장
             * TODO 세션 저장
             */

            /**
             * 게임 방 저장
             */
            if (gameRooms.containsKey(gameRoom.getId())) {
                gameRooms.replace(gameRoom.getId(), gameRoom);
            } else {
                ReflectionUtils.setField(idField, gameRoom, ++gameRoomSeq);
                gameRooms.put(gameRoom.getId(), gameRoom);
            }

            return gameRoom;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException();
        }
    }

    private void saveAll(Iterable<GameRoomParticipant> participants) {
        try {
            Field gameRoomParticipantIdField = GameRoomParticipant.class.getDeclaredField("id");
            gameRoomParticipantIdField.setAccessible(true);

            for (GameRoomParticipant participant : participants) {
                if (participant.getId() == null) {
                    ReflectionUtils.setField(gameRoomParticipantIdField, participant, ++gameRoomParticipantSeq);
                }

                if (gameRoomParticipants.containsKey(participant.getId())) {
                    gameRoomParticipants.replace(participant.getId(), participant);
                } else {
                    gameRoomParticipants.put(participant.getId(), participant);
                }
            }
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException();
        }
    }
}
