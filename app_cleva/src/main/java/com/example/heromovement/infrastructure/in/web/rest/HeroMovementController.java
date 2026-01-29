package com.example.heromovement.infrastructure.in.web.rest;

import com.example.heromovement.application.ports.in.MoveHeroUseCase;
import com.example.heromovement.domain.model.Position;
import com.example.heromovement.infrastructure.in.web.request.HeroMovementRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


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

        logger.info("Received request to move hero: instructionsPath={}, mapPath={}",
                request.instructionsPath(), request.mapPath());

        String mapPath = request.mapPath();
        if (mapPath != null && mapPath.isEmpty()) {
            mapPath = null;
        }

        Position finalPosition = moveHeroUseCase.execute(
                request.instructionsPath(),
                mapPath
        );

        return ResponseEntity.ok(finalPosition);
    }


    @PostMapping(
            value = "/move/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Position> moveUpload(
            @RequestPart("file") MultipartFile instructionsFile,
            @RequestParam(required = false) String mapPath
    ) throws IOException {

        if (instructionsFile == null || instructionsFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Path tmp = Files.createTempFile("instr-upload-", ".txt");

        try {
            logger.info("Received upload to move hero: fileName={}, mapPath={}",
                    instructionsFile.getOriginalFilename(), mapPath);

            instructionsFile.transferTo(tmp.toFile());

            if (mapPath != null && mapPath.isEmpty()) {
                mapPath = null;
            }

            Position finalPosition = moveHeroUseCase.execute(tmp.toString(), mapPath);
            return ResponseEntity.ok(finalPosition);

        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}


