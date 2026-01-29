package com.example.heromovement.domain.ports.out;

import com.example.heromovement.domain.model.InstructionSet;
import java.io.IOException;

public interface InstructionRepositoryPort {
    InstructionSet load(String resourcePath) throws IOException;
}
