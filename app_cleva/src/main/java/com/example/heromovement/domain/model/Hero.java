package com.example.heromovement.domain.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record Hero(Position currentPosition) {

    private static final Logger logger = LoggerFactory.getLogger(Hero.class);

    public Hero move(Direction direction, GameMap map) {
        int nextX = currentPosition.x() + direction.getDx();
        int nextY = currentPosition.y() + direction.getDy();
        if (map.isValidMove(nextX, nextY)) {
            logger.info("Moving hero to ({}, {})", nextX, nextY);
            return new Hero(new Position(nextX, nextY));
        }
        logger.info("Blocked move to ({}, {})", nextX, nextY);
        return this;
    }
}
