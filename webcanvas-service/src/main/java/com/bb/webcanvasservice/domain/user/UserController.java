package com.bb.webcanvasservice.domain.user;

import com.bb.webcanvasservice.domain.user.dto.request.UserCreateRequest;
import com.bb.webcanvasservice.domain.user.dto.response.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 유저 API의 endpoint
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;


    /**
     * 클라이언트의 정보를 받아 유저를 생성한다.
     * @param userCreateRequest 생성 요청 정보
     * @return userDto 생성된 유저 정보
     */
    @PostMapping
    public ResponseEntity<UserDto> registerNewUser(@RequestBody UserCreateRequest userCreateRequest) {
        User user = userService.createUser(userCreateRequest.clientFingerprint());
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUserToken()));
    }

}
