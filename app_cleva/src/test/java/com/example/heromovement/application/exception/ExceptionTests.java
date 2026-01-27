package com.example.heromovement.application.exception;

import com.example.heromovement.application.service.HeroMovementService;
import com.example.heromovement.common.exception.InvalidCoordinateException;
import com.example.heromovement.common.exception.InvalidInstructionException;
import com.example.heromovement.common.exception.MapLoadingException;
import com.example.heromovement.common.exception.InvalidMapException;
import com.example.heromovement.domain.model.GameMap;
import com.example.heromovement.domain.model.Position;
import com.example.heromovement.infrastructure.adapter.out.persistence.FileMapLoader;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionTests {

    @Test
    void invalidCoordinate_whenStartOutOfBounds_throwsInvalidCoordinateException() throws Exception {
        FileMapLoader loader = new FileMapLoader("carte.txt");
        HeroMovementService service = new HeroMovementService(loader);
        assertThrows(InvalidCoordinateException.class, () ->
                service.execute(new Position(100, 100), "N", "carte.txt"));
    }

    @Test
    void invalidInstruction_whenUnknownChar_throwsInvalidInstructionException() throws Exception {
        FileMapLoader loader = new FileMapLoader("carte.txt");
        HeroMovementService service = new HeroMovementService(loader);
        assertThrows(InvalidInstructionException.class, () ->
                service.execute(new Position(0, 0), "X", "carte.txt"));
    }

    @Test
    void mapLoading_whenPathMissing_throwsMapLoadingException() throws Exception {
        FileMapLoader loader = new FileMapLoader("carte.txt");
        HeroMovementService service = new HeroMovementService(loader);
        assertThrows(MapLoadingException.class, () ->
                service.execute(new Position(0, 0), "N", "nonexistent_file.txt"));
    }

    @Test
    void invalidMap_whenEmptyLines_throwsInvalidMapException() {
        assertThrows(InvalidMapException.class, () -> GameMap.from(Collections.emptyList()));
    }
}
