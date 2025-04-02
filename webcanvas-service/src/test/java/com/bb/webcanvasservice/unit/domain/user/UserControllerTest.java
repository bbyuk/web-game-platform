package com.bb.webcanvasservice.unit.domain.user;

import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserController;
import com.bb.webcanvasservice.domain.user.UserService;
import com.bb.webcanvasservice.domain.user.dto.request.UserCreateRequest;
import com.bb.webcanvasservice.security.web.WebSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {UserController.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class) // 단위테스트에서는 Spring Security 관련 항목 제외
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("유저 생성 API - 유저를 생성한다")
    void createUser() throws Exception {
        // given
        String fingerprint = "3f8d47a3a92b77e5";
        User expectedReturnEntity = new User(fingerprint);

        BDDMockito.given(userService.createUser(fingerprint))
                .willReturn(expectedReturnEntity);

        UserCreateRequest userCreateRequest = new UserCreateRequest(fingerprint);

        // when
        mockMvc
                .perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateRequest))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userToken").value(fingerprint));

        // then
    }

}