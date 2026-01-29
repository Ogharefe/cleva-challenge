package com.example.heromovement.application.ports.in;

import com.example.heromovement.domain.model.Position;

public interface MoveHeroUseCase {
    Position execute( String instructionsPath, String mapPath);
}
