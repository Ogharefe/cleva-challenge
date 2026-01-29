package com.example.heromovement.infrastructure.in.web;

import com.example.heromovement.application.ports.in.MoveHeroUseCase;
import com.example.heromovement.domain.model.Position;
import com.example.heromovement.infrastructure.in.web.request.HeroMovementRequest;
import com.example.heromovement.infrastructure.in.web.rest.HeroMovementController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HeroMovementController.class) // Specify the controller
class HeromovementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean // This provides the mock to the Controller and prevents the NPE
    private MoveHeroUseCase moveHeroUseCase;

    private final String MAP_FILE = "carte.txt";
    private final String INSTR_FILE_1 = "instructions1.txt";
    private final String INSTR_FILE_2 = "instructions2.txt";

    @Test
    @DisplayName("Scenario 1: Starting at (3,0) moves to (9,2)")
    void testScenario1() throws Exception {
        // Arrange
        // We define the mock return value to match the expected outcome of Scenario 1
        when(moveHeroUseCase.execute(INSTR_FILE_1, MAP_FILE))
                .thenReturn(new Position(9, 2));

        HeroMovementRequest request = new HeroMovementRequest(INSTR_FILE_1, MAP_FILE);

        // Act & Assert
        mockMvc.perform(post("/api/hero/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(9))
                .andExpect(jsonPath("$.y").value(2));
    }

    @Test
    @DisplayName("Scenario 2: Starting at (6,9) moves to (7,5)")
    void testScenario2() throws Exception {
        // Arrange
        // We define the mock return value to match the expected outcome of Scenario 2
        when(moveHeroUseCase.execute(INSTR_FILE_2, MAP_FILE))
                .thenReturn(new Position(6, 9));

        HeroMovementRequest request = new HeroMovementRequest(INSTR_FILE_2, MAP_FILE);

        // Act & Assert
        mockMvc.perform(post("/api/hero/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(6))
                .andExpect(jsonPath("$.y").value(9));
    }
}