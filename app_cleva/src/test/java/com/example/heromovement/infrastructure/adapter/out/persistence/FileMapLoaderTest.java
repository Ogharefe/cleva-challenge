package com.example.heromovement.infrastructure.adapter.out.persistence;

import com.example.heromovement.common.exception.MapLoadingException;
import com.example.heromovement.domain.model.GameMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileMapLoaderTest {

    @Test
    void loadMap_fromClasspath_simpleName_succeeds() throws Exception {
        FileMapLoader loader = new FileMapLoader("carte.txt");
        GameMap map = loader.loadMap("carte.txt");
        assertNotNull(map);
    }

    @Test
    void loadMap_fromFilesystem_absolutePath_succeeds() throws Exception {
        Path tmp = Files.createTempFile("map-", ".txt");
        Files.write(tmp, List.of("#####", "#   #", "#####"), StandardCharsets.UTF_8);
        FileMapLoader loader = new FileMapLoader(tmp.toString());
        GameMap map = loader.loadMap(tmp.toString());
        assertNotNull(map);
        Files.deleteIfExists(tmp);
    }

    @Test
    void loadMap_fromConfiguredFilesystem_whenArgumentMissing_succeeds() throws Exception {
        Path tmp = Files.createTempFile("map-", ".txt");
        Files.write(tmp, List.of("#####", "#   #", "#####"), StandardCharsets.UTF_8);
        FileMapLoader loader = new FileMapLoader(tmp.toString());
        GameMap map = loader.loadMap(null);
        assertNotNull(map);
        Files.deleteIfExists(tmp);
    }

    @Test
    void loadMap_missingClasspath_thenConfiguredFilesystemMissing_throwsMapLoadingException() {
        FileMapLoader loader = new FileMapLoader("carte.txt");
        assertThrows(MapLoadingException.class, () -> loader.loadMap("does-not-exist.txt"));
    }
}
