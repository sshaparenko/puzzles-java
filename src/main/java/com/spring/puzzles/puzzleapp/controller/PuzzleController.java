package com.spring.puzzles.puzzleapp.controller;

import com.spring.puzzles.puzzleapp.common.ApiResponse;
import com.spring.puzzles.puzzleapp.service.PuzzleAutoSolve;
import com.spring.puzzles.puzzleapp.service.PuzzleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class PuzzleController {
    private final PuzzleService puzzleService;
    private final PuzzleAutoSolve autoSolve;

    @PostMapping("/upload")
    public String uploadImage (@RequestParam MultipartFile image) {
        if (image.isEmpty()) return "redirect:/no-file.html";
        puzzleService.saveImage(image);
        return "redirect:/index.html";
    }

    @GetMapping("/puzzle")
    public ResponseEntity<ApiResponse> getPuzzle (@RequestParam Integer rows, @RequestParam Integer columns, @RequestParam String filename) {
        try {
            puzzleService.setImage(filename);
            String json = puzzleService.savePuzzles(rows, columns, filename);
            return new ResponseEntity<>(new ApiResponse(true, json), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(new ApiResponse(false, "The IO Exception has been occurred!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/auto-solve")
    @ResponseBody
    public ResponseEntity<InputStreamResource> autoSolve(@RequestParam String imageName) {
        try {
            InputStream result = autoSolve.solve(imageName);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new InputStreamResource(result));
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
