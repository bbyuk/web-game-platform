package com.bb.webcanvasservice.user.infrastructure.persistence.mapper;

import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;

/**
 * entity <-> domain model
 * User
 */
public class UserModelMapper {
    public static User toUser(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getFingerprint(), entity.getState(), entity.getRefreshToken());
    }

    public static UserJpaEntity toUserJpaEntity(User user) {
        return new UserJpaEntity(user.getId(), user.getFingerprint(), user.getRefreshToken(), user.getState());
    }
}
