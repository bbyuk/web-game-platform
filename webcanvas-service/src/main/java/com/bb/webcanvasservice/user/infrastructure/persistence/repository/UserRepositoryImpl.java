package com.bb.webcanvasservice.user.infrastructure.persistence.repository;

import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.model.UserState;
import com.bb.webcanvasservice.user.application.repository.UserRepository;
import com.bb.webcanvasservice.user.infrastructure.persistence.mapper.UserModelEntityMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final EntityManager em;

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId)
                .map(UserModelEntityMapper::toUser);
    }

    @Override
    public User save(User user) {
        return UserModelEntityMapper.toUser(
                userJpaRepository.save(UserModelEntityMapper.toUserJpaEntity(user))
        );
    }

    @Override
    public Optional<User> findByFingerprint(String fingerprint) {
        return userJpaRepository.findByFingerprint(fingerprint)
                .map(UserModelEntityMapper::toUser);
    }

    @Override
    public UserState findUserState(Long userId) {
        return userJpaRepository.findUserState(userId);
    }

    @Override
    public void updateUsersStates(List<Long> userIds, UserState state) {
        String jpql = """
                update  UserJpaEntity u
                set     u.state =   :state
                where   u.id    in  :ids
                """;

        em.createQuery(jpql)
                .setParameter("state", state)
                .setParameter("ids", userIds)
                .executeUpdate();

        em.clear();
    }
}
