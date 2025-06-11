package com.bb.webcanvasservice.infrastructure.persistence.user.repository;

import com.bb.webcanvasservice.domain.user.model.User;
import com.bb.webcanvasservice.domain.user.model.UserStateCode;
import com.bb.webcanvasservice.domain.user.repository.UserRepository;
import com.bb.webcanvasservice.infrastructure.persistence.user.UserModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId)
                .map(UserModelMapper::toUser);
    }

    @Override
    public User save(User user) {
        return UserModelMapper.toUser(
                userJpaRepository.save(UserModelMapper.toUserJpaEntity(user))
        );
    }

    @Override
    public Optional<User> findByFingerprint(String fingerprint) {
        return userJpaRepository.findByFingerprint(fingerprint)
                .map(UserModelMapper::toUser);
    }

    @Override
    public UserStateCode findUserState(Long userId) {
        return userJpaRepository.findUserState(userId);
    }
}
