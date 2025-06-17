package com.bb.webcanvasservice.user.presentation.controller;

import com.bb.webcanvasservice.user.application.service.UserApplicationService;
import com.bb.webcanvasservice.common.security.Authenticated;
import com.bb.webcanvasservice.common.security.WebCanvasAuthentication;
import com.bb.webcanvasservice.user.presentation.mapper.UserPresentationDtoMapper;
import com.bb.webcanvasservice.user.presentation.request.UserCreateRequest;
import com.bb.webcanvasservice.user.presentation.response.UserInfoResponse;
import com.bb.webcanvasservice.user.presentation.response.UserStateInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 유저 API의 endpoint
 */
@Tag(name = "User API", description = "유저의 등록 및 조회 등 유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserApplicationService userApplicationService;

    /**
     * 유저 생성
     * 
     * 클라이언트의 정보를 받아 유저를 생성한다.
     * @param userCreateRequest 생성 요청 정보
     * @return userDto 생성된 유저 정보
     */
    @PostMapping
    @Operation(summary = "유저 등록", description = "클라이언트의 정보를 받아 유저를 생성한다.")
    public ResponseEntity<UserInfoResponse> createNewUser(@RequestBody UserCreateRequest userCreateRequest) {
        return ResponseEntity.ok(
                UserPresentationDtoMapper.toUserInfoResponse(
                        userApplicationService.createUser(userCreateRequest.clientFingerprint())
                )
        );
    }

    @GetMapping("state")
    @Operation(summary = "유저 상태 조회", description = "유저의 현재 상태를 조회한다.")
    public ResponseEntity<UserStateInfoResponse> findUserState(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                UserPresentationDtoMapper.toUserStateInfoResponse(
                        userApplicationService.findUserState(authentication.getUserId())
                )
        );
    }
}
