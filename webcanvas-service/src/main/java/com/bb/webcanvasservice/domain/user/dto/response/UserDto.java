package com.bb.webcanvasservice.domain.user.dto.response;

/**
 * 유저 정보 리턴 dto
 * @param userId 유저 식별 ID
 * @param userToken
 */
public record UserDto(
        Long userId,
        String userToken
) {}
