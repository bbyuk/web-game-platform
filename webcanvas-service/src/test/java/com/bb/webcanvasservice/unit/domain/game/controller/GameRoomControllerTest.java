package com.bb.webcanvasservice.unit.domain.game.controller;

import com.bb.webcanvasservice.common.util.JwtManager;
import com.bb.webcanvasservice.presentation.game.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomJpaRepository;
import com.bb.webcanvasservice.domain.game.service.GameRoomFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("[unit] [presentation] 게임 API Controller WebMvcTest 단위테스트")
class GameRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GameRoomFacade gameRoomFacade;

    @MockitoBean
    private GameRoomJpaRepository gameRoomRepository;

    @MockitoBean
    private JwtManager jwtManager;

    private SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("dwqoijsdkfjsdoifjdskfjosdifjdsoifjifewofijqe"));

    @Test
    @DisplayName("방 만들기 API - 방을 만들고, 요청자는 해당 방에 입장한다.")
    void createGameRoom() throws Exception {
        // given1
        long userId = 1L;
        long gameRoomId = 9999L;
        long gameRoomEntranceId = 21313L;
        long expiration = 3600000; // 1시간 (ms)
        String fingerprint = "qwdoiasjdassa";
        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("fingerprint", fingerprint)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();;

        BDDMockito.given(gameRoomFacade.createGameRoomAndEnter(any()))
                .willReturn(new GameRoomEntranceResponse(gameRoomId, gameRoomEntranceId));

        BDDMockito.given(jwtManager.generateToken(any(), any(), anyLong()))
                .willReturn(token);
        BDDMockito.given(jwtManager.resolveBearerTokenValue(any()))
                .willReturn(token);


        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/game/canvas/room")
                .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtManager.BEARER_TOKEN, String.format("%s %s", JwtManager.BEARER_TOKEN_PREFIX
                                , jwtManager.generateToken(userId, fingerprint, expiration)))
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameRoomId").value(gameRoomId))
                .andExpect(jsonPath("$.gameRoomEntranceId").value(gameRoomEntranceId));



        // then
    }
}