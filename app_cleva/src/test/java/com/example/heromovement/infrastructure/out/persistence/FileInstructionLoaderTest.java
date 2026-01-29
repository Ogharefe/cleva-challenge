package com.example.heromovement.infrastructure.out.persistence;

import com.example.heromovement.domain.model.InstructionSet;
import com.example.heromovement.infrastructure.adapter.FileInstructionLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileInstructionLoaderTest {

    @Test
    void loadFromClasspath_simpleName() throws IOException {
        FileInstructionLoader loader = new FileInstructionLoader("", "");
        InstructionSet set = loader.load("instructions1.txt");
        assertNotNull(set);
        assertEquals(3, set.start().x());
        assertEquals(0, set.start().y());
        assertEquals("SSSSEEEEEENN", set.instructions());
    }

    @Test
    void loadFromFilesystem_absolutePath() throws IOException {
        Path tmp = Files.createTempFile("instr-", ".txt");
        Files.writeString(tmp, "(6,9)\nOONOOOSSO", StandardCharsets.UTF_8);

        FileInstructionLoader loader = new FileInstructionLoader(tmp.toString(), "");
        InstructionSet set = loader.load(tmp.toString());
        assertNotNull(set);
        assertEquals(6, set.start().x());
        assertEquals(9, set.start().y());
        assertEquals("OONOOOSSO", set.instructions());
    }

    @Test
    void loadFromConfiguredDefault_whenArgEmpty() throws IOException {
        Path tmp = Files.createTempFile("instr-default-", ".txt");
        Files.writeString(tmp, "7 5\nNNSS", StandardCharsets.UTF_8);

        FileInstructionLoader loader = new FileInstructionLoader(tmp.toString(), "");
        InstructionSet set = loader.load(null);
        assertNotNull(set);
        assertEquals(7, set.start().x());
        assertEquals(5, set.start().y());
        assertEquals("NNSS", set.instructions());
    }

    @Test
    void loadFromConfiguredDirectory_withSimpleName() throws IOException {
        Path dir = Files.createTempDirectory("instr-dir-");
        Path file = dir.resolve("runA.txt");
        Files.writeString(file, "(2,1)\nWENS", StandardCharsets.UTF_8);

        FileInstructionLoader loader = new FileInstructionLoader("", dir.toString());
        InstructionSet set = loader.load("runA.txt");
        assertNotNull(set);
        assertEquals(2, set.start().x());
        assertEquals(1, set.start().y());
        assertEquals("WENS", set.instructions());
    }
}
