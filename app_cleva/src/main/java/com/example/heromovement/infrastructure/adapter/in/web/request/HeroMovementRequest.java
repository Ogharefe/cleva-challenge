package com.example.heromovement.infrastructure.adapter.in.web.request;

public record HeroMovementRequest(int startX, int startY, String instructions, String mapPath) {
}
