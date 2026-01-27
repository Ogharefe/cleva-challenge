package com.example.heromovement.infrastructure.adapter.in.web;

import com.example.heromovement.application.ports.in.MoveHeroUseCase;
import com.example.heromovement.domain.model.Position;
import com.example.heromovement.infrastructure.adapter.in.web.request.HeroMovementRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/hero")
public class HeroMovementController {

    private static final Logger logger = LoggerFactory.getLogger(HeroMovementController.class);

    private final MoveHeroUseCase moveHeroUseCase;
    public HeroMovementController(MoveHeroUseCase moveHeroUseCase) {
        this.moveHeroUseCase = moveHeroUseCase;
    }

    @PostMapping("/move")
    public ResponseEntity<Position> move(@RequestBody HeroMovementRequest request) {
        logger.info("Received request to move hero: startX={}, startY={}, instructions={}, mapPath={}", 
                request.startX(), request.startY(), request.instructions(), request.mapPath());
        
        String mapPath = request.mapPath();
        if (mapPath != null && mapPath.isEmpty()) {
            mapPath = null;
        }
        Position finalPosition = moveHeroUseCase.execute(new Position(request.startX(), request.startY()), request.instructions(), mapPath);
        return ResponseEntity.ok(finalPosition);
    }
}
