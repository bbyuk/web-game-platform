package com.bb.webcanvasservice.small.game.stub.service;

import com.bb.webcanvasservice.game.application.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.domain.model.gameroom.*;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameGameRoomRepositoryStub implements GameRoomRepository {

    private long gameRoomSeq = 0L;

    private long gameRoomParticipantSeq = 0L;
    private long gameSessionSeq = 0L;
    private long gameTurnSeq = 0L;


    private Map<Long, GameRoom> userGameRoomMap = new HashMap<>();
    private Map<Long, GameRoom> gameRooms = new HashMap<>();
    private Map<Long, GameSession> gameSessions = new HashMap<>();

    private Map<Long, GameTurn> gameTurns = new HashMap<>();

    private Map<Long, GameRoomParticipant> gameRoomParticipants = new HashMap<>();

    @Override
    public Optional<GameRoom> findGameRoomById(Long gameRoomId) {
        return Optional.of(gameRooms.get(gameRoomId));
    }

    @Override
    public Optional<GameRoom> findGameRoomByGameRoomParticipantId(Long gameRoomParticipantId) {
        GameRoomParticipant gameRoomParticipant = gameRoomParticipants.get(gameRoomParticipantId);
        return Optional.of(gameRooms.get(gameRoomParticipant.getGameRoomId()));
    }

    @Override
    public Optional<GameRoom> findGameRoomByGameSessionId(Long gameSessionId) {
        GameSession gameSession = gameSessions.get(gameSessionId);
        return findGameRoomById(gameSession.getGameRoomId());
    }

    @Override
    public Optional<GameRoom> findCurrentJoinedGameRoomByUserId(Long userId) {
        return Optional.of(userGameRoomMap.get(userId));
    }

    @Override
    public boolean existsJoinCodeConflictOnActiveGameRoom(String joinCode) {
        return false;
    }

    @Override
    public List<GameRoom> findGameRoomsByCapacityAndGameRoomStateAndGameRoomParticipantState(int gameRoomCapacity, GameRoomState gameRoomState, GameRoomParticipantState gameRoomParticiapantState) {
        return gameRooms.values().stream()
                .filter(
                        gameRoom -> gameRoom.getCurrentParticipants().size() < gameRoomCapacity
                                && gameRoomState == gameRoom.getState())
                .collect(Collectors.toList());
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
            saveAllGameRoomParticipants(gameRoom.getParticipants());

            /**
             * 게임 방 저장
             */
            if (!gameRooms.containsKey(gameRoom.getId())) {
                if (gameRoom.getId() == null) {
                    ReflectionUtils.setField(idField, gameRoom, ++gameRoomSeq);
                }

                gameRooms.put(gameRoom.getId(), gameRoom);
            }

            /**
             * 게임 세션 저장
             */
            saveGameSession(gameRoom.getGameSession());

            /**
             * 게임 턴 목록 저장
             */
            if (gameRoom.getGameSession() != null) {
                saveAllGameTurns(gameRoom.getGameSession().getGameTurns());
            }

            return gameRoom;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException();
        }
    }

    private void saveGameSession(GameSession gameSession) {
        try {
            if (gameSession != null) {
                if (gameSession.getId() == null) {
                    Field idField = GameSession.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    ReflectionUtils.setField(idField, gameSession, ++gameSessionSeq);
                }

                if (!gameSessions.containsKey(gameSession.getId())) {
                    gameSessions.put(gameSession.getId(), gameSession);
                }
            }
        }
        catch(Exception e) {
            throw new IllegalStateException();
        }
    }

    private void saveAllGameRoomParticipants(Iterable<GameRoomParticipant> participants) {
        try {
            Field gameRoomParticipantIdField = GameRoomParticipant.class.getDeclaredField("id");
            gameRoomParticipantIdField.setAccessible(true);

            for (GameRoomParticipant participant : participants) {
                if (participant.getId() == null) {
                    ReflectionUtils.setField(gameRoomParticipantIdField, participant, ++gameRoomParticipantSeq);
                }

                if (!gameRoomParticipants.containsKey(participant.getId())) {
                    gameRoomParticipants.put(participant.getId(), participant);
                }
            }
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException();
        }
    }

    private void saveAllGameTurns(Iterable<GameTurn> turns) {
        try {
            Field gameTurnIdField = GameTurn.class.getDeclaredField("id");
            gameTurnIdField.setAccessible(true);

            for (GameTurn gameTurn : turns) {
                if (gameTurn.getId() == null) {
                    ReflectionUtils.setField(gameTurnIdField, gameTurn, ++gameTurnSeq);
                }

                if (!gameTurns.containsKey(gameTurn.getId())) {
                    gameTurns.put(gameTurn.getId(), gameTurn);
                }
            }
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException();
        }
    }
}
