package com.bb.webcanvasservice.user.presentation.mapper;

import com.bb.webcanvasservice.user.application.dto.UserDto;
import com.bb.webcanvasservice.user.application.dto.UserStateDto;
import com.bb.webcanvasservice.user.presentation.response.UserInfoResponse;
import com.bb.webcanvasservice.user.presentation.response.UserStateInfoResponse;

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
