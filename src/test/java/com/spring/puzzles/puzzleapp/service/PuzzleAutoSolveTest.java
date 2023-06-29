package com.spring.puzzles.puzzleapp.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

class PuzzleAutoSolveTest {
    private PuzzleAutoSolve puzzleAutoSolve;
    private PuzzleService puzzleService;

    @BeforeEach
    void initUseCase() {
        puzzleAutoSolve = new PuzzleAutoSolve();
        puzzleService = new PuzzleService();
        puzzleService.setDirPath("src/test/resources/images/");
        puzzleAutoSolve.setPuzzlesDir("src/test/resources/images/puzzles/");
        puzzleAutoSolve.setImagesDir("src/test/resources/images/");
    }

    @Test
    void solve() throws IOException {
        int count = 0;
        puzzleService.setImage("blank.jpg");
        puzzleService.savePuzzles(2,2, "blank.jpg");
        InputStream result = puzzleAutoSolve.solve("blank.jpg");
        File[] files = new File("src/test/resources/images/puzzles").listFiles();

        Assertions.assertThat(files).isNotEmpty();
        Assertions.assertThat(result).isNotNull();

        do {
            Files.delete(Path.of("src/test/resources/images/puzzles/blank/" + count + ".png"));
        } while (++count < 4);

        Files.deleteIfExists(Path.of("src/test/resources/images/puzzles/blank"));
    }
}