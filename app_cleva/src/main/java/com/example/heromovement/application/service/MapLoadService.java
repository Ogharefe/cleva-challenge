package com.example.heromovement.application.service;

import com.example.heromovement.application.ports.in.LoadMapUseCase;
import com.example.heromovement.common.exception.MapLoadingException;
import com.example.heromovement.domain.model.GameMap;
import com.example.heromovement.domain.ports.out.MapRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MapLoadService implements LoadMapUseCase {

    private static final Logger logger = LoggerFactory.getLogger(MapLoadService.class);

    private final MapRepositoryPort mapRepositoryPort;

    public MapLoadService(MapRepositoryPort mapRepositoryPort) {
        this.mapRepositoryPort = mapRepositoryPort;
    }

    @Override
    public GameMap load(String mapPath) {
        try {
            logger.info("Loading map: path={}", mapPath);
            return mapRepositoryPort.loadMap(mapPath);
        } catch (IOException e) {
            logger.error("Failed to load map file: {}", mapPath, e);
            throw new MapLoadingException("Failed to load map file: " + mapPath, e);
        }
    }
}

