package com.spring.puzzles.puzzleapp.controller;

import com.spring.puzzles.puzzleapp.common.ApiResponse;
import com.spring.puzzles.puzzleapp.service.PuzzleAutoSolve;
import com.spring.puzzles.puzzleapp.service.PuzzleService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PuzzleControllerTest {
    @Mock
    private PuzzleService puzzleService;
    @Mock
    private PuzzleAutoSolve puzzleAutoSolve;

    private PuzzleController puzzleController;

    @BeforeEach
    void initUseCase() {
        puzzleController = new PuzzleController(puzzleService, puzzleAutoSolve);
    }

    @Test
    void uploadImage() {
        MockMultipartFile multipartFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", new byte[]{1,2,3});
        String response = puzzleController.uploadImage(multipartFile);
        Assertions.assertThat(response).isEqualTo("redirect:/index.html");
    }

    @Test
    void uploadImageEmptyCase() {
        String response = puzzleController.uploadImage(new MockMultipartFile("test", new byte[]{}));
        Assertions.assertThat(response).isEqualTo("redirect:/no-file.html");
    }

    @Test
    void getPuzzle() {
        ResponseEntity<ApiResponse> response = puzzleController.getPuzzle(5,5,"test");
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void autoSolve() throws IOException {
        InputStream testStream = new ByteArrayInputStream(new byte[]{1});
        when(puzzleAutoSolve.solve(any(String.class))).thenReturn(testStream);

        ResponseEntity<InputStreamResource> response = puzzleController.autoSolve("test");
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getInputStream()).isEqualTo(testStream);
    }
}