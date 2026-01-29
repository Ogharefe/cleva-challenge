package com.example.heromovement.infrastructure.in.web;

import com.example.heromovement.application.ports.in.LoadMapUseCase;
import com.example.heromovement.domain.model.GameMap;
import com.example.heromovement.infrastructure.in.web.rest.MapController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MapController.class)
class MapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoadMapUseCase loadMapUseCase;

    @Test
    void shouldLoadMapAndReturnInfo() throws Exception {
        GameMap map = GameMap.from(List.of("#####", "#   #", "#####"));
        given(loadMapUseCase.load(eq("carte.txt"))).willReturn(map);

        String requestBody = """
                { "path": "carte.txt" }
                """;

        mockMvc.perform(post("/api/map/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.width").value(map.width()))
                .andExpect(jsonPath("$.height").value(map.height()));
    }
}
