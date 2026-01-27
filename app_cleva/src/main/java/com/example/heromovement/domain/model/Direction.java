package com.example.heromovement.domain.model;
import com.example.heromovement.common.exception.InvalidInstructionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Direction {
    N(0, -1),
    S(0, 1),
    E(1, 0),
    O(-1, 0);

    private final int dx;
    private final int dy;
    private static final Logger logger = LoggerFactory.getLogger(Direction.class);

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }

    public static Direction fromChar(char c) {
        char up = Character.toUpperCase(c);
        return switch (up) {
            case 'N' -> N;
            case 'S' -> S;
            case 'E' -> E;
            case 'O' -> O;
            default -> {
                logger.warn("Invalid direction: {}", c);
                throw new InvalidInstructionException("Invalid direction: " + c);
            }
        };
    }
}
