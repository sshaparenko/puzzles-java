package com.spring.puzzles.puzzleapp.service;

import com.spring.puzzles.puzzleapp.Puzzle;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Getter
@Setter
@Slf4j
public class PuzzleAutoSolve {
    private String puzzlesDir;
    private String imagesDir;
    private Set<Puzzle> visitedPuzzles;
    private int threshold;
    private double maxDiff;

    public PuzzleAutoSolve() {
        puzzlesDir = "src/main/resources/static/images/puzzles/";
        imagesDir = "src/main/resources/static/images/";
        visitedPuzzles = new HashSet<>();
        threshold = 50;
        maxDiff = 10;
    }

    /**
     *
     * This method is utilizing all the methods to solve the puzzles
     * @param originalImageName name of the original image
     * @return InputStream of bytes of the result image
     * @throws IOException
     */
    public InputStream solve(String originalImageName) throws IOException {
        log.info("Solving process has started...");

        LinkedList<BufferedImage> images = getFiles(puzzlesDir + originalImageName.split("\\.")[0]);
        Map<Integer, BufferedImage> resultMap = new HashMap<>();
        List<Puzzle> puzzlesList = new ArrayList<>();

        if (images.size() == 0) {
            log.warn("No files were found! Try to generate them on /api/v1/puzzle");
            throw new IOException();
        }

        File origialImageFile = new File(imagesDir + originalImageName);
        BufferedImage originalImage = ImageIO.read(origialImageFile);

        int puzzleHeight = images.get(0).getHeight();
        int puzzleWidth = images.get(0).getWidth();
        int rows = originalImage.getHeight() / puzzleHeight;
        int columns = originalImage.getWidth() / puzzleWidth;

        images.forEach(image -> puzzlesList.add(new Puzzle(image)));

        setAdjacentPuzzle(puzzlesList, rows, columns);
        Optional<Puzzle> startingPuzzle = findCorner(puzzlesList, columns);

        log.info("Starting the traversal process...");
        dfsTraversal(startingPuzzle, columns);
        log.info("Traversal was finished successfully!");

        visitedPuzzles.forEach(puzzle -> resultMap.put(puzzle.getIndex(), puzzle.getImagePuzzle()));
        visitedPuzzles.clear();

        BufferedImage resultImage = createImage(originalImage.getWidth(), originalImage.getHeight(), puzzleWidth, puzzleHeight, resultMap);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resultImage, "jpg", outputStream);
        outputStream.flush();
        byte[] imageBytes = outputStream.toByteArray();
        outputStream.close();

        log.info("Solving process has finished...");
        return new ByteArrayInputStream(imageBytes);
    }

    /**
     *
     * Find the corner puzzle and sets index for it based on the number of columns
     * @param puzzlesList list of puzzles
     * @param columns number of columns
     * @return optional of Puzzle object
     */
    private Optional<Puzzle> findCorner(List<Puzzle> puzzlesList, int columns) {
        log.info("Finding the corner puzzle...");
        for (Puzzle puzzle : puzzlesList) {
            if (puzzle.isCorner() && puzzle.getBottom().isPresent() && puzzle.getLeft().isPresent()) {
                puzzle.setIndex(columns - 1);
                log.info("Corner was found!");
                return Optional.of(puzzle);
            }
            if (puzzle.isCorner() && puzzle.getBottom().isPresent() && puzzle.getRight().isPresent()) {
                puzzle.setIndex(0);
                log.info("Corner was found!");
                return Optional.of(puzzle);
            }
            if (puzzle.isCorner() && puzzle.getTop().isPresent() && puzzle.getRight().isPresent()) {
                puzzle.setIndex(columns * columns - columns);
                log.info("Corner was found!");
                return Optional.of(puzzle);
            }
            if (puzzle.isCorner() && puzzle.getTop().isPresent() && puzzle.getLeft().isPresent()) {
                puzzle.setIndex(columns * columns - 1);
                log.info("Corner was found!");
                return Optional.of(puzzle);
            }
        }
        log.info("Corner was NOT found!");
        return Optional.empty();
    }

    /**
     *
     * Based on the calculated differences, set the top, right, bottom, and left adjacent puzzle fpr each puzzle of list
     * @param puzzlesList list of all puzzles
     * @param rows number of rows
     * @param columns number of columns
     */
    private void setAdjacentPuzzle(List<Puzzle> puzzlesList, int rows, int columns) {
        log.info("Setting adjacent puzzles...");
        for (int x = 0; x < rows * columns; x++) {
            double[] diffs = {maxDiff, maxDiff, maxDiff, maxDiff};

            Puzzle puzzle = puzzlesList.get(x);
            BufferedImage firstPuzzle = puzzle.getImagePuzzle();

            for (int y = 0; y < rows * columns; y++) {
                Puzzle secondPuzzleObj = puzzlesList.get(y);
                if (secondPuzzleObj == puzzle) continue;
                BufferedImage secondPuzzle = secondPuzzleObj.getImagePuzzle();

                double currentTopDiff = getTopDifferance(firstPuzzle, secondPuzzle);
                double currentRightDiff = getRightDifferance(firstPuzzle, secondPuzzle);
                double currentBottomDiff = getBottomDifferance(firstPuzzle, secondPuzzle);
                double currentLeftDiff = getLeftDifferance(firstPuzzle, secondPuzzle);

                if (currentTopDiff < diffs[0]) {
                    diffs[0] = currentTopDiff;
                    puzzle.setTop(Optional.of(secondPuzzleObj));
                }
                if (currentRightDiff < diffs[1]) {
                    diffs[1] = currentRightDiff;
                    puzzle.setRight(Optional.of(secondPuzzleObj));
                }
                if (currentBottomDiff < diffs[2]) {
                    diffs[2] = currentBottomDiff;
                    puzzle.setBottom(Optional.of(secondPuzzleObj));
                }
                if (currentLeftDiff < diffs[3]) {
                    diffs[3] = currentLeftDiff;
                    puzzle.setLeft(Optional.of(secondPuzzleObj));
                }
            }
        }
        log.info("All puzzles were set!");
    }

    /**
     *
     * Method is traversing the top, right, bottom, and left puzzles of the specified corner puzzle
     * @param optionalPuzzle corner puzzle
     * @param columns number of puzzle
     */
    private void dfsTraversal(Optional<Puzzle> optionalPuzzle, int columns) {
        if (optionalPuzzle.isEmpty() || visitedPuzzles.contains(optionalPuzzle.get())) {
            return;
        }
        Puzzle puzzle = optionalPuzzle.get();
        visitedPuzzles.add(puzzle);
        Optional<Puzzle> topPuzzle = puzzle.getTop();
        if (topPuzzle.isPresent()) {
            topPuzzle.get().setIndex(puzzle.getIndex() - columns);
            dfsTraversal(topPuzzle, columns);
        }
        Optional<Puzzle> rightPuzzle = puzzle.getRight();
        if (rightPuzzle.isPresent()) {
            rightPuzzle.get().setIndex(puzzle.getIndex() + 1);
            dfsTraversal(rightPuzzle, columns);
        }
        Optional<Puzzle> bottomPuzzle = puzzle.getBottom();
        if (bottomPuzzle.isPresent()) {
            bottomPuzzle.get().setIndex(puzzle.getIndex() + columns);
            dfsTraversal(bottomPuzzle, columns);
        }
        Optional<Puzzle> leftPuzzle = puzzle.getLeft();
        if (leftPuzzle.isPresent()) {
            leftPuzzle.get().setIndex(puzzle.getIndex() - 1);
            dfsTraversal(leftPuzzle, columns);
        }
    }


    /**
     * Generates the result image from puzzles dictionary (index, puzzle)
     *
     * @param width    total width
     * @param height   total height
     * @param filesMap dictionary of puzzles and indexes
     * @return image generated from puzzles
     */
    private BufferedImage createImage(int width, int height, int puzzleWidth, int puzzleHeight, Map<Integer, BufferedImage> filesMap) {
        log.info("Creating the final image...");
        int rows = height / puzzleHeight;
        int columns = width / puzzleWidth;
        BufferedImage solvedPuzzle = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = solvedPuzzle.createGraphics();

        int count = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                g2d.drawImage(filesMap.get(count), puzzleWidth * j, puzzleHeight * i, puzzleWidth, puzzleHeight, null);
                count++;
            }
        }

        g2d.dispose();
        log.info("Final image was created successfully!");
        return solvedPuzzle;
    }

    /**
     * Access images of puzzles in the specified dir and put them into List.
     *
     * @param dirPath path do the directory, where all the puzzles stored
     * @return list of puzzles of type BufferedImage
     */
    private LinkedList<BufferedImage> getFiles(String dirPath) {
        log.info("Reading files...");
        File dir = new File(dirPath);

        File[] files = dir.listFiles();
        if (files != null) {
            LinkedList<BufferedImage> resultList = Arrays.stream(files).map(file -> {
                try {
                    return ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("There is an exception while reading files!");
                    return null;
                }
            }).collect(Collectors.toCollection(LinkedList::new));
            log.info("Files were read successfully");
            return resultList;
        }
        log.info("Files was not read successfully!");
        return new LinkedList<>();
    }

    private double getTopBottomDifferance(BufferedImage firstPuzzle, BufferedImage secondPuzzle, int startY1, int startY2) {
        int width1 = firstPuzzle.getWidth();

        int diffCount = 0;

        for (int x = 0; x < width1; x++) {
            int rgb1 = firstPuzzle.getRGB(x, startY1);
            int rgb2 = secondPuzzle.getRGB(x, startY2);

            Color color1 = new Color(rgb1);
            Color color2 = new Color(rgb2);

            int redDiff = Math.abs(color1.getRed() - color2.getRed());
            int greenDiff = Math.abs(color1.getGreen() - color2.getGreen());
            int blueDiff = Math.abs(color1.getBlue() - color2.getBlue());

            if (redDiff > threshold || greenDiff > threshold || blueDiff > threshold) {
                diffCount++;
            }
        }
        return (double) diffCount / width1 * 100.0;
    }

    private double getLeftRightDifferance(BufferedImage firstPuzzle, BufferedImage secondPuzzle, int startX1, int startX2) {
        int height1 = firstPuzzle.getHeight();

        int diffCount = 0;

        for (int y = 0; y < height1; y++) {
            int rgb1 = firstPuzzle.getRGB(startX1, y);
            int rgb2 = secondPuzzle.getRGB(startX2, y);

            Color color1 = new Color(rgb1);
            Color color2 = new Color(rgb2);

            int redDiff = Math.abs(color1.getRed() - color2.getRed());
            int greenDiff = Math.abs(color1.getGreen() - color2.getGreen());
            int blueDiff = Math.abs(color1.getBlue() - color2.getBlue());

            if (redDiff > threshold || greenDiff > threshold || blueDiff > threshold) {
                diffCount++;
            }
        }
        return (double) diffCount / height1 * 100.0;
    }

    private double getTopDifferance(BufferedImage firstPuzzle, BufferedImage secondPuzzle) {
        return getTopBottomDifferance(firstPuzzle, secondPuzzle, 0, firstPuzzle.getHeight() - 1);
    }

    private double getBottomDifferance(BufferedImage firstPuzzle, BufferedImage secondPuzzle) {
        return getTopBottomDifferance(firstPuzzle, secondPuzzle, firstPuzzle.getHeight() - 1, 0);
    }

    private double getLeftDifferance(BufferedImage firstPuzzle, BufferedImage secondPuzzle) {
        return getLeftRightDifferance(firstPuzzle, secondPuzzle, 0, firstPuzzle.getWidth() - 1);
    }

    private double getRightDifferance(BufferedImage firstPuzzle, BufferedImage secondPuzzle) {
        return getLeftRightDifferance(firstPuzzle, secondPuzzle, firstPuzzle.getWidth() - 1, 0);
    }

}
