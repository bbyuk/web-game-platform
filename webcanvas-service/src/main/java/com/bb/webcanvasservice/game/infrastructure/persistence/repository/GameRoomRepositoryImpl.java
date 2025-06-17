package com.bb.webcanvasservice.game.infrastructure.persistence.repository;

import com.bb.webcanvasservice.game.domain.model.GameRoom;
import com.bb.webcanvasservice.game.domain.model.GameRoomEntranceState;
import com.bb.webcanvasservice.game.domain.model.GameRoomState;
import com.bb.webcanvasservice.game.domain.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.infrastructure.persistence.mapper.GameModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GameRoomRepositoryImpl implements GameRoomRepository {
    private final GameRoomJpaRepository gameRoomJpaRepository;

    @Override
    public Optional<GameRoom> findById(Long gameRoomId) {
        return gameRoomJpaRepository.findById(gameRoomId).map(GameModelMapper::toModel);
    }

    @Override
    public Optional<GameRoom> findNotClosedGameRoomByUserId(Long userId) {
        return gameRoomJpaRepository.findNotClosedGameRoomByUserId(userId)
                .map(GameModelMapper::toModel);
    }

    @Override
    public boolean existsJoinCodeConflictOnActiveGameRoom(String joinCode) {
        return gameRoomJpaRepository.existsJoinCodeConflictOnActiveGameRoom(joinCode);
    }

    @Override
    public List<GameRoom> findGameRoomsByCapacityAndStateWithEntranceState(int gameRoomCapacity, List<GameRoomState> enterableStates, GameRoomEntranceState activeEntranceState) {
        return gameRoomJpaRepository.findGameRoomsByCapacityAndStateWithEntranceState(gameRoomCapacity, enterableStates, activeEntranceState)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameRoom> findByState(GameRoomState state) {
        return gameRoomJpaRepository.findByState(state)
                .stream()
                .map(GameModelMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GameRoom> findRoomWithJoinCodeForEnter(String joinCode) {
        return gameRoomJpaRepository.findRoomWithJoinCodeForEnter(joinCode)
                .map(GameModelMapper::toModel);
    }

    @Override
    public GameRoom save(GameRoom gameRoom) {
        return GameModelMapper.toModel(gameRoomJpaRepository.save(GameModelMapper.toEntity(gameRoom)));
    }
}
