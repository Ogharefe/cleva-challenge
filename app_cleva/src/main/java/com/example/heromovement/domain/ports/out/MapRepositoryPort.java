package com.example.heromovement.domain.ports.out;

import com.example.heromovement.domain.model.GameMap;
import java.io.IOException;

public interface MapRepositoryPort {
    GameMap loadMap(String resourcePath) throws IOException;
}
