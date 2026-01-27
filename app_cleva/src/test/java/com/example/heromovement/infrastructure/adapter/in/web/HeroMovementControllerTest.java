package com.example.heromovement.infrastructure.adapter.in.web;

import com.example.heromovement.application.ports.in.MoveHeroUseCase;
import com.example.heromovement.domain.model.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HeroMovementController.class)
class HeroMovementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MoveHeroUseCase moveHeroUseCase;

    @Test
    void shouldMoveHero() throws Exception {
        Position expectedPosition = new Position(9, 2);
        given(moveHeroUseCase.execute(any(Position.class), eq("SSSSEEEEEENN"), eq("carte.txt")))
                .willReturn(expectedPosition);

        String requestBody = """
                {
                    "startX": 3,
                    "startY": 0,
                    "instructions": "SSSSEEEEEENN",
                    "mapPath": "carte.txt"
                }
                """;

        mockMvc.perform(post("/api/hero/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(9))
                .andExpect(jsonPath("$.y").value(2));

    }

    @Test
    void shouldNotMoveHeroWhenThereIsBlocage() throws Exception {
        Position expectedPosition = new Position(6, 9);
        given(moveHeroUseCase.execute(any(Position.class), eq("OONOOOSSO"), eq("carte.txt")))
                .willReturn(expectedPosition);

        String requestBody = """
                {
                    "startX": 6,
                    "startY": 9,
                    "instructions": "OONOOOSSO",
                    "mapPath": "carte.txt"
                }
                """;

        mockMvc.perform(post("/api/hero/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(6))
                .andExpect(jsonPath("$.y").value(9));

    }
}
