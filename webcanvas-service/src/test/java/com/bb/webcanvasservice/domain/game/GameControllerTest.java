package com.bb.webcanvasservice.domain.game;

import com.bb.webcanvasservice.domain.game.dto.response.GameRoomCreateResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.security.auth.JwtManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {GameController.class},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GameRoomService gameRoomService;

    @MockitoBean
    private GameRoomRepository gameRoomRepository;

    @MockitoBean
    private JwtManager jwtManager;


    @Test
    @DisplayName("방 만들기 API - 방을 만들고, 요청자는 해당 방에 입장한다.")
    void createGameRoom() throws Exception {
        // given1
        long userId = 1L;
        long gameRoomId = 9999L;
        long gameRoomEntranceId = 21313L;
        long expiration = 3600000; // 1시간 (ms)
        String fingerprint = "qwdoiasjdassa";
        String token = new JwtManager().generateToken(userId, fingerprint, expiration);

        BDDMockito.given(gameRoomService.createGameRoomAndEnter(any()))
                .willReturn(new GameRoomEntranceResponse(gameRoomId, gameRoomEntranceId));

        BDDMockito.given(jwtManager.generateToken(any(), any(), anyLong()))
                .willReturn(token);
        BDDMockito.given(jwtManager.resolveToken(any()))
                .willReturn(token);
        BDDMockito.given(jwtManager.validateToken(any()))
                .willReturn(true);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/game/canvas/room")
                .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtManager.BEARER_TOKEN, String.format("%s %s", JwtManager.TOKEN_PREFIX
                                , jwtManager.generateToken(userId, fingerprint, expiration)))
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameRoomId").value(gameRoomId))
                .andExpect(jsonPath("$.gameRoomEntranceId").value(gameRoomEntranceId));



        // then
    }
}