package com.spring.puzzles.puzzleapp.controller;

import com.spring.puzzles.puzzleapp.common.ApiResponse;
import com.spring.puzzles.puzzleapp.service.PuzzleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class PuzzleController {
    private final PuzzleService puzzleService;
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadImage (@RequestParam MultipartFile image) {
        try {
            puzzleService.saveImage(image);
            return new ResponseEntity<>(new ApiResponse(true, "Image was uploaded successfully!"), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/puzzle")
    public ResponseEntity<ApiResponse> getPuzzle (@RequestParam Integer rows, @RequestParam Integer columns, @RequestParam String filename) {
        try {
            puzzleService.setImage("src/main/resources/images/" + filename);
            String json = puzzleService.savePuzzles(rows, columns, filename);
            System.out.println(json);
            return new ResponseEntity<>(new ApiResponse(true, json), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(new ApiResponse(false, "The IO Exception has been occurred!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
