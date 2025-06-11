package com.bb.webcanvasservice.infrastructure.persistence.user;

import com.bb.webcanvasservice.domain.user.entity.User;
import com.bb.webcanvasservice.domain.user.model.UserStateCode;
import com.bb.webcanvasservice.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByFingerprint(String fingerprint) {
        return userJpaRepository.findByFingerprint(fingerprint);
    }

    @Override
    public UserStateCode findUserState(Long userId) {
        return userJpaRepository.findUserState(userId);
    }
}
