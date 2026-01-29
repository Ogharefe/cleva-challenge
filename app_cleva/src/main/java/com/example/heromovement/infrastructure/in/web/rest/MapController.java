package com.example.heromovement.infrastructure.in.web.rest;

import com.example.heromovement.application.ports.in.LoadMapUseCase;
import com.example.heromovement.domain.model.GameMap;
import com.example.heromovement.infrastructure.in.web.request.MapInfo;
import com.example.heromovement.infrastructure.in.web.request.MapLoadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/map")
public class MapController {

    private static final Logger logger = LoggerFactory.getLogger(MapController.class);

    private final LoadMapUseCase loadMapUseCase;

    public MapController(LoadMapUseCase loadMapUseCase) {
        this.loadMapUseCase = loadMapUseCase;
    }

    @PostMapping("/load")
    public ResponseEntity<MapInfo> load(@RequestBody MapLoadRequest request) {
        String path = request.path();
        if (path != null && path.isEmpty()) {
            path = null;
        }
        logger.info("Received request to load map: path={}", path);
        GameMap map = loadMapUseCase.load(path);
        MapInfo info = new MapInfo(map.width(), map.height());
        return ResponseEntity.ok(info);
    }
}
