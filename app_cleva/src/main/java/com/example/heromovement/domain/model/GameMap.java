package com.example.heromovement.domain.model;

import java.util.List;
import com.example.heromovement.common.exception.InvalidMapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record GameMap(char[][] grid, int width, int height) {

    private static final Logger logger = LoggerFactory.getLogger(GameMap.class);

    public static GameMap from(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new InvalidMapException("Map cannot be empty");
        }
        int h = lines.size();
        int w = lines.stream().mapToInt(String::length).max().orElse(0);
        char[][] g = new char[h][w];
        for (int y = 0; y < h; y++) {
            String line = lines.get(y);
            for (int x = 0; x < w; x++) {
                g[y][x] = x < line.length() ? line.charAt(x) : ' ';
            }
        }
        return new GameMap(g, w, h);
    }

    public boolean isValidMove(int x, int y) {
        boolean valid = x >= 0 && x < width && y >= 0 && y < height && grid[y][x] != '#';
        logger.debug("Validating move ({}, {}): {}", x, y, valid);
        return valid;
    }
}
