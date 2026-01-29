package com.example.heromovement.application.exception;

import com.example.heromovement.application.service.HeroMovementService;
import com.example.heromovement.common.exception.InvalidCoordinateException;
import com.example.heromovement.common.exception.InvalidInstructionException;
import com.example.heromovement.common.exception.MapLoadingException;
import com.example.heromovement.common.exception.InvalidMapException;
import com.example.heromovement.domain.model.GameMap;
import com.example.heromovement.domain.model.InstructionSet;
import com.example.heromovement.domain.model.Position;
import com.example.heromovement.domain.ports.out.MapRepositoryPort;
import com.example.heromovement.infrastructure.adapter.FileMapLoader;
import com.example.heromovement.domain.ports.out.InstructionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExceptionTests {

    static class TestInstructionRepo implements InstructionRepositoryPort {
        private final String instructions;
        TestInstructionRepo(String instructions) {
            this.instructions = instructions;
        }
        @Override
        public InstructionSet load(String resourcePath) {
            Position start = new Position(0, 0);
            return new InstructionSet(start, instructions);
        }
    }


    @Test
    void invalidInstruction_whenUnknownChar_throwsInvalidInstructionException() {
        FileMapLoader loader = new FileMapLoader("carte.txt");
        HeroMovementService service = new HeroMovementService(loader, new TestInstructionRepo("X"));

        assertThrows(InvalidInstructionException.class, () ->
                service.execute("instructions.txt", "carte.txt"));
    }


    @Test
    void mapLoading_whenPathMissing_throwsMapLoadingException() throws Exception {
        FileMapLoader loader = new FileMapLoader("carte.txt");
        HeroMovementService service = new HeroMovementService(loader, new TestInstructionRepo("N"));
        assertThrows(MapLoadingException.class, () ->
                service.execute("instructions.txt", "nonexistent_file.txt"));
    }

    @Test
    void invalidMap_whenEmptyLines_throwsInvalidMapException() {
        assertThrows(InvalidMapException.class, () -> GameMap.from(Collections.emptyList()));
    }
}
