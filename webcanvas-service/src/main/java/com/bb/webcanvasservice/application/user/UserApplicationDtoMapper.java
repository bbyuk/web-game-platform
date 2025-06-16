package com.bb.webcanvasservice.application.user;

import com.bb.webcanvasservice.application.user.dto.UserDto;
import com.bb.webcanvasservice.application.user.dto.UserStateDto;
import com.bb.webcanvasservice.domain.user.model.User;
import com.bb.webcanvasservice.domain.user.model.UserState;

/**
 * domain model -> application layer dto mapper
 */
public class UserApplicationDtoMapper {
    public static UserDto toUserDto(User userJpaEntity) {
        return new UserDto(userJpaEntity.getId(), userJpaEntity.getFingerprint());
    }
    public static UserStateDto toUserStateDto(UserState userState) {
        return new UserStateDto(userState);
    }
}
