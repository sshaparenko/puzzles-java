package com.spring.puzzles.puzzleapp;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.util.Optional;

@Getter
@Setter
public class Puzzle {
    private BufferedImage imagePuzzle;
    private Optional<Puzzle> top;
    private Optional<Puzzle> right;
    private Optional<Puzzle> bottom;
    private Optional<Puzzle> left;
    private int index;

    public Puzzle() {
        top = Optional.empty();
        right = Optional.empty();
        bottom = Optional.empty();
        left = Optional.empty();
    }

    public Puzzle(BufferedImage imagePuzzle) {
        this.imagePuzzle = imagePuzzle;
        top = Optional.empty();
        right = Optional.empty();
        bottom = Optional.empty();
        left = Optional.empty();
    }

    public boolean isCorner() {
        boolean hasTop = top.isPresent();
        boolean hasRight = right.isPresent();
        boolean hasLeft = left.isPresent();
        boolean hasBottom = bottom.isPresent();

        return (hasTop && hasRight && !hasLeft && !hasBottom) ||
                (hasTop && !hasRight && hasLeft && !hasBottom) ||
                (!hasTop && hasRight && !hasLeft && hasBottom) ||
                (!hasTop && !hasRight && hasLeft && hasBottom);
    }
}
