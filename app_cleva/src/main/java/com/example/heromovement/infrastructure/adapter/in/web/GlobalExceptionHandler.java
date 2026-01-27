package com.example.heromovement.infrastructure.adapter.in.web;

import com.example.heromovement.common.exception.InvalidInstructionException;
import com.example.heromovement.common.exception.InvalidMapException;
import com.example.heromovement.common.exception.MapLoadingException;
import com.example.heromovement.common.exception.InvalidCoordinateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidInstructionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidInstruction(InvalidInstructionException ex) {
        logger.warn("Invalid instruction: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Invalid instruction");
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidMapException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMap(InvalidMapException ex) {
        logger.warn("Invalid map: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Invalid map");
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MapLoadingException.class)
    public ResponseEntity<Map<String, Object>> handleMapLoading(MapLoadingException ex) {
        logger.error("Map loading error", ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Map loading error");
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidCoordinateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCoordinate(InvalidCoordinateException ex) {
        logger.warn("Invalid coordinate: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Invalid coordinate");
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
