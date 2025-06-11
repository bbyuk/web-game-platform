package com.bb.webcanvasservice.presentation.user;

import com.bb.webcanvasservice.application.user.dto.UserDto;
import com.bb.webcanvasservice.application.user.dto.UserStateDto;
import com.bb.webcanvasservice.presentation.user.response.UserInfoResponse;
import com.bb.webcanvasservice.presentation.user.response.UserStateInfoResponse;

/**
 * application layer dto -> presentation layer dto mapping
 * 유저 정보 API 응답
 */
public class UserPresentationDtoMapper {
    public static UserInfoResponse toUserInfoResponse(UserDto userDto) {
        return new UserInfoResponse(userDto.userId(), userDto.fingerprint());
    }

    public static UserStateInfoResponse toUserStateInfoResponse(UserStateDto userStateDto) {
        return new UserStateInfoResponse(userStateDto.state());
    }
}
