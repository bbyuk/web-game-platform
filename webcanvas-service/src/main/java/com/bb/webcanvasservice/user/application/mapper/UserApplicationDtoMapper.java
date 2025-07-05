package com.bb.webcanvasservice.user.application.mapper;

import com.bb.webcanvasservice.user.application.dto.UserDto;
import com.bb.webcanvasservice.user.application.dto.UserStateDto;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.model.UserState;

/**
 * domain model -> application layer dto mapper
 */
public class UserApplicationDtoMapper {
    public static UserDto toUserDto(User userJpaEntity) {
        return new UserDto(userJpaEntity.id(), userJpaEntity.fingerprint());
    }
    public static UserStateDto toUserStateDto(UserState userState) {
        return new UserStateDto(userState);
    }
}
