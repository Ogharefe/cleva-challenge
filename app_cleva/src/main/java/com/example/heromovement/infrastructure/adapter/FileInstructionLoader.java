package com.example.heromovement.infrastructure.adapter;

import com.example.heromovement.common.exception.MapLoadingException;
import com.example.heromovement.domain.model.InstructionSet;
import com.example.heromovement.domain.model.Position;
import com.example.heromovement.domain.ports.out.InstructionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileInstructionLoader implements InstructionRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(FileInstructionLoader.class);

    private final String instructionsFilePath;
    private final String instructionsDir;

    public FileInstructionLoader(
            @Value("${app.instructions.path:}") String instructionsFilePath,
            @Value("${app.instructions.dir:}") String instructionsDir) {

        this.instructionsFilePath = instructionsFilePath;
        this.instructionsDir = instructionsDir;
    }

    @Override
    public InstructionSet load(String filePath) throws IOException {
        logger.debug("instructions filePath argument = [{}]", filePath);
        logger.debug("instructionsFilePath (Spring) = [{}]", instructionsFilePath);

        if (filePath != null && !filePath.isBlank()) {
            boolean hintFilesystem = filePath.contains("/") || filePath.contains("\\") || Paths.get(filePath).isAbsolute();
            if (!hintFilesystem) {
                if (instructionsDir != null && !instructionsDir.isBlank()) {
                    Path candidate = Paths.get(instructionsDir, filePath).toAbsolutePath().normalize();
                    if (Files.exists(candidate) && Files.isReadable(candidate)) {
                        logger.debug("Loading instructions from FILESYSTEM (dir): {}", candidate);
                        try (BufferedReader reader = Files.newBufferedReader(candidate, StandardCharsets.UTF_8)) {
                            java.util.List<String> lines = reader.lines().toList();
                            InstructionSet set = parseLines(lines);
                            if (set == null) {
                                throw new MapLoadingException("Invalid instructions format in filesystem: " + candidate, null);
                            }
                            return set;
                        } catch (IOException e) {
                            logger.error("I/O error reading filesystem instructions [{}]: {}", candidate, e.getMessage());
                            throw new MapLoadingException("I/O error loading filesystem instructions: " + candidate, e);
                        }
                    } else {
                        logger.debug("Instructions not present/readable in configured dir: {}", candidate);
                    }
                }
                ClassPathResource resource = new ClassPathResource(filePath);
                if (resource.exists()) {
                    logger.debug("Loading instructions from CLASSPATH: {}", filePath);
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                        java.util.List<String> lines = reader.lines().toList();
                        InstructionSet set = parseLines(lines);
                        if (set == null) {
                            throw new MapLoadingException("Invalid instructions format in classpath: " + filePath, null);
                        }
                        return set;
                    } catch (IOException e) {
                        logger.error("I/O error reading classpath instructions [{}]: {}", filePath, e.getMessage());
                        throw new MapLoadingException("I/O error loading classpath instructions: " + filePath, e);
                    }
                } else {
                    logger.warn("Instructions not found in classpath: {}", filePath);
                    Path localFs = Paths.get(filePath).toAbsolutePath().normalize();
                    if (Files.exists(localFs) && Files.isReadable(localFs)) {
                        logger.debug("Loading instructions from FILESYSTEM (simple file): {}", localFs);
                        try (BufferedReader reader = Files.newBufferedReader(localFs, StandardCharsets.UTF_8)) {
                            java.util.List<String> lines = reader.lines().toList();
                            InstructionSet set = parseLines(lines);
                            if (set == null) {
                                throw new MapLoadingException("Invalid instructions format in filesystem: " + localFs, null);
                            }
                            return set;
                        } catch (IOException e) {
                            logger.error("I/O error reading filesystem instructions [{}]: {}", localFs, e.getMessage());
                            throw new MapLoadingException("I/O error loading filesystem instructions: " + localFs, e);
                        }
                    }
                }
            } else {
                Path pathArg = Paths.get(filePath).toAbsolutePath().normalize();
                logger.debug("Loading instructions from FILESYSTEM (arg): {}", pathArg);
                if (!Files.exists(pathArg)) {
                    logger.error("Instructions file not found at filesystem path: {}", pathArg);
                    throw new MapLoadingException("Instructions file not found: " + pathArg, null);
                }
                if (!Files.isReadable(pathArg)) {
                    logger.error("Instructions file is not readable: {}", pathArg);
                    throw new MapLoadingException("Instructions file not readable: " + pathArg, null);
                }
                try (BufferedReader reader = Files.newBufferedReader(pathArg, StandardCharsets.UTF_8)) {
                    java.util.List<String> lines = reader.lines().toList();
                    InstructionSet set = parseLines(lines);
                    if (set == null) {
                        throw new MapLoadingException("Invalid instructions format in filesystem: " + pathArg, null);
                    }
                    return set;
                } catch (IOException e) {
                    logger.error("I/O error reading filesystem instructions [{}]: {}", pathArg, e.getMessage());
                    throw new MapLoadingException("I/O error loading filesystem instructions: " + pathArg, e);
                }
            }
        }

        Path fsPath = Paths.get(instructionsFilePath).toAbsolutePath().normalize();
        logger.debug("Trying filesystem instructions path: {}", fsPath);

        if (!Files.exists(fsPath)) {
            logger.error("Instructions file not found at filesystem path: {}", fsPath);
            throw new MapLoadingException("Instructions file not found: " + fsPath, null);
        }

        if (!Files.isReadable(fsPath)) {
            logger.error("Instructions file is not readable: {}", fsPath);
            throw new MapLoadingException("Instructions file not readable: " + fsPath, null);
        }

        logger.debug("Loading instructions from FILESYSTEM: {}", fsPath);
        try (BufferedReader reader = Files.newBufferedReader(fsPath, StandardCharsets.UTF_8)) {
            java.util.List<String> lines = reader.lines().toList();
            InstructionSet set = parseLines(lines);
            if (set == null) {
                throw new MapLoadingException("Invalid instructions format in filesystem: " + fsPath, null);
            }
            return set;
        } catch (IOException e) {
            logger.error("I/O error reading filesystem instructions [{}]: {}", fsPath, e.getMessage());
            throw new MapLoadingException("I/O error loading filesystem instructions: " + fsPath, e);
        }
    }

    private InstructionSet parseLines(java.util.List<String> lines) {
        if (lines == null || lines.size() < 2) {
            return null;
        }
        String first = lines.get(0).trim();
        String second = lines.get(1).trim();
        Position start = parsePosition(first);
        if (start == null || second.isEmpty()) {
            return null;
        }
        return new InstructionSet(start, second);
    }

    private Position parsePosition(String s) {
        String cleaned = s.replace("(", "").replace(")", "").replace(",", " ").trim();
        String[] parts = cleaned.split("\\s+");
        if (parts.length < 2) return null;
        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return new Position(x, y);
        } catch (NumberFormatException e) {
            logger.error("Invalid start position format: {}", s);
            return null;
        }
    }
}
