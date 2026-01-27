package com.example.heromovement.application.ports.in;

import com.example.heromovement.domain.model.GameMap;

public interface LoadMapUseCase {
    GameMap load(String mapPath);
}

