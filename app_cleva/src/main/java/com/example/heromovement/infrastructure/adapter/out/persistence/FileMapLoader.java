package com.example.heromovement.infrastructure.adapter.out.persistence;

import com.example.heromovement.domain.model.GameMap;
import com.example.heromovement.domain.ports.out.MapRepositoryPort;
import com.example.heromovement.common.exception.MapLoadingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileMapLoader implements MapRepositoryPort {

    @Autowired
    private Environment env;

    private final String mapFilePath;
    private static final Logger logger = LoggerFactory.getLogger(FileMapLoader.class);

    @Autowired
    public FileMapLoader(@Value("${app.map.path}") String mapFilePath) {
        this.mapFilePath = mapFilePath;
    }

    @Override
    public GameMap loadMap(String filePath) throws IOException {

        logger.debug("filePath argument = [{}]", filePath);
        logger.debug("mapFilePath (Spring) = [{}]", mapFilePath);

        // -------------------------------------------------
        //  Try CLASSPATH first (using argument)
        // -------------------------------------------------
        if (filePath != null && !filePath.isBlank()) {
            ClassPathResource resource = new ClassPathResource(filePath);

            if (resource.exists()) {
                logger.debug("Loading map from CLASSPATH: {}", filePath);
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                    return GameMap.from(reader.lines().toList());
                } catch (IOException e) {
                    logger.error("I/O error reading classpath resource [{}]: {}", filePath, e.getMessage());
                    throw new MapLoadingException("I/O error loading classpath map: " + filePath, e);
                }
            } else {
                logger.warn("Not found in classpath: {}", filePath);
            }
        }
        // -------------------------------------------------
        // Fall back to FILESYSTEM using Spring path
        // -------------------------------------------------
        Path fsPath = Paths.get(mapFilePath).toAbsolutePath().normalize();

        logger.debug("Trying filesystem path: {}", fsPath);

        if (!Files.exists(fsPath)) {
            logger.error("Map file not found at filesystem path: {}", fsPath);
            throw new MapLoadingException("Map file not found: " + fsPath, null);
        }

        if (!Files.isReadable(fsPath)) {
            logger.error("Map file is not readable: {}", fsPath);
            throw new MapLoadingException("Map file not readable: " + fsPath, null);
        }
        logger.debug("Loading map from FILESYSTEM: {}", fsPath);
        try (BufferedReader reader = Files.newBufferedReader(fsPath, StandardCharsets.UTF_8)) {
            return GameMap.from(reader.lines().toList());
        } catch (IOException e) {
            logger.error("I/O error reading filesystem map [{}]: {}", fsPath, e.getMessage());
            throw new MapLoadingException("I/O error loading filesystem map: " + fsPath, e);
        }
    }
}

