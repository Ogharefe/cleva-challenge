package com.example.heromovement.application.service;

import com.example.heromovement.application.ports.in.MoveHeroUseCase;
import com.example.heromovement.common.exception.InvalidInstructionException;
import com.example.heromovement.domain.model.*;
import com.example.heromovement.domain.ports.out.MapRepositoryPort;
import com.example.heromovement.domain.ports.out.InstructionRepositoryPort;
import org.springframework.stereotype.Service;
import com.example.heromovement.common.exception.MapLoadingException;
import com.example.heromovement.common.exception.InvalidCoordinateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Service
public class HeroMovementService implements MoveHeroUseCase {

    private final MapRepositoryPort mapRepositoryPort;
    private final InstructionRepositoryPort instructionRepositoryPort;
    private static final Logger logger = LoggerFactory.getLogger(HeroMovementService.class);

    public HeroMovementService(MapRepositoryPort mapRepositoryPort,
                               InstructionRepositoryPort instructionRepositoryPort) {
        this.mapRepositoryPort = mapRepositoryPort;
        this.instructionRepositoryPort = instructionRepositoryPort;
    }

    @Override
    public Position execute(String instructionsPath, String mapPath) {

        try {
            var instructionSet = instructionRepositoryPort.load(instructionsPath);

            if (instructionSet.start() == null) {
                throw new InvalidCoordinateException("Missing start position in instruction file");
            }

            Position start = instructionSet.start();
            String instructions = instructionSet.instructions();

            logger.info("Executing move: start=({}, {}), instructionsLength={}, mapPath={}",
                    start.x(),
                    start.y(),
                    instructions != null ? instructions.length() : 0,
                    mapPath);


            GameMap map = mapRepositoryPort.loadMap(mapPath);

            boolean inBounds = start.x() >= 0 && start.x() < map.width()
                    && start.y() >= 0 && start.y() < map.height();

            if (!inBounds) {
                logger.warn("Invalid starting coordinate: ({}, {}) for map size {}x{}",
                        start.x(), start.y(), map.width(), map.height());
                throw new InvalidCoordinateException(
                        "Invalid starting coordinate: (" + start.x() + "," + start.y() + ")"
                );
            }

            Hero hero = new Hero(start);

            for (char instruction : instructions.toCharArray()) {
                Direction direction = Direction.fromChar(instruction);

                if (direction == null) {
                    throw new InvalidInstructionException("Invalid instruction: " + instruction);
                }
                hero = hero.move(direction, map);
            }

            Position finalPosition = hero.currentPosition();
            logger.info("Final position after move: ({}, {})", finalPosition.x(), finalPosition.y());

            return finalPosition;

        } catch (IOException e) {
            logger.error("Failed to load file(s): map={}, instructions={}",
                    mapPath, instructionsPath, e);
            throw new MapLoadingException("Failed to load map or instructions file", e);
        }
    }
}
