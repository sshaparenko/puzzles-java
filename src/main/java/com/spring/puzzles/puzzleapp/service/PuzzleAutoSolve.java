package com.spring.puzzles.puzzleapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 *
 * The puzzle auto-solve algorithm.
 *
 * <br/>Class has six methods:
 * <ul>
 *     <li>getDifferance() - calculate the difference
 *     between two images. It is used to compare the
 *     original image with generated image.
 *     </li>
 *     <li></li>
 *     <li></li>
 *     <li></li>
 *     <li></li>
 *     <li></li>
 * </ul>
 *
 * */

@Component
@Slf4j
public class PuzzleAutoSolve {

    /**
     *
     * @param originalImageName name of the original image
     * @return InputStream of bytes of the result image
     * @throws IOException
     */
    public InputStream solve(String originalImageName) throws IOException {
        log.info("Solving process has started...");

        List<BufferedImage> filesList = getFiles("src/main/resources/static/images/puzzles/" + originalImageName.split("\\.")[0]);

        if (filesList.size() == 0) {
            log.warn("No files were found! Try to generate them on /api/v1/puzzle");
            throw new IOException();
        }

        File origialImageFile = new File("src/main/resources/static/images/" + originalImageName);
        BufferedImage originalImage = ImageIO.read(origialImageFile);

        int puzzleHeight = filesList.get(0).getHeight();
        int puzzleWidth = filesList.get(0).getWidth();
        int rows = originalImage.getHeight() / puzzleHeight;
        int columns = originalImage.getWidth() / puzzleWidth;

        Map<Integer, BufferedImage> resultMap = new HashMap<>();

        for (BufferedImage image : filesList) {
            int count = 0;
            int min_count = 0;
            double min_differance = 100.0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    BufferedImage imageToCompare = createImageToCompare(image, originalImage.getWidth(), originalImage.getHeight(), j, i);
                    double current_differance = getDifference(imageToCompare, originalImage);
                    if (min_differance > current_differance) {
                        min_differance = current_differance;
                        min_count = count;
                    }
                    count++;
                }
            }
            resultMap.put(min_count, image);
        }
        BufferedImage resultImage = createImage(originalImage.getWidth(), originalImage.getHeight(), resultMap);
        //saveImage(resultImage);
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
     * Generates the result image from puzzles dictionary (index, puzzle)
     * @param width total width
     * @param height total height
     * @param filesMap dictionary of puzzles and indexes
     * @return image generated from puzzles
     */
    private BufferedImage createImage(int width, int height, Map<Integer, BufferedImage> filesMap) {
        log.info("Creating the final image...");
        int puzzleHeight = filesMap.get(0).getHeight();
        int puzzleWidth = filesMap.get(0).getWidth();
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
     *
     * Generates an image from the specified puzzle with width and height equal to the values of original image.
     * row and column parameters are used to set the position of the puzzle on image.
     * As a result, this method generates an image with width and height of the original image and puzzle on it,
     * which will change its position according to the values of row and colum.
     * So, in each iteration we will have a puzzle set to its position and a black space around it.
     * @param tile BufferedImage of puzzle
     * @param width total width of the image
     * @param height total height of the image
     * @param column value of the column
     * @param row value of the row
     * @return generated image
     */
    private BufferedImage createImageToCompare(BufferedImage tile, int width, int height, int column, int row) {
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        int puzzleHeight = tile.getHeight();
        int puzzleWidth = tile.getWidth();
        Graphics2D g2d = resultImage.createGraphics();
        g2d.drawImage(tile, puzzleWidth * column, puzzleHeight * row, puzzleWidth, puzzleHeight, null);
        g2d.dispose();
        return resultImage;
    }

    /**
     *
     * Save the image to resources
     * @param image image to save
     */
    private void saveImage(BufferedImage image) {
        log.info("Saving the result...");
        try {
            File outputFile = new File("src/main/resources/", "test.jpg");
            ImageIO.write(image, "jpg", outputFile);
            log.info("Result image was saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Access images of puzzles in the specified dir and put them into List.
     * @param dirPath path do the directory, where all the puzzles stored
     * @return list of puzzles of type BufferedImage
     */
    public List<BufferedImage> getFiles(String dirPath) {
        log.info("Starting the read of files...");
        File dir = new File(dirPath);

        File[] files = dir.listFiles();
        if (files != null) {
            List<BufferedImage> resultList = Arrays.stream(files).map(file -> {
                try {
                    return ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("There is an exception while reading files!");
                    return null;
                }
            }).toList();
            log.info("Files were read successfully");
            return resultList;
        }
        log.info("Files was not read successfully!");
        return new ArrayList<>();
    }

    /**
     *
     * Calculates the difference between images (original and generated). This difference is used in the solve() method
     * in order to find the position of the puzzle.
     * @param imageToCompare generated image, that should be compared with the original
     * @param original original image, that should be compared with the generated one
     * @return percentage of difference
     */
    private double getDifference(BufferedImage imageToCompare, BufferedImage original) {
        int width1 = imageToCompare.getWidth();
        int height1 = imageToCompare.getHeight();
        int width2 = original.getWidth();
        int height2 = original.getHeight();

        if (width1 != width2 || height1 != height2) {
            throw new IllegalArgumentException("Images must have the same dimensions");
        }

        int numPixels = width1 * height1;
        int diffCount = 0;

        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                int rgb1 = imageToCompare.getRGB(x, y);
                int rgb2 = original.getRGB(x, y);

                Color color1 = new Color(rgb1);
                Color color2 = new Color(rgb2);

                int redDiff = Math.abs(color1.getRed() - color2.getRed());
                int greenDiff = Math.abs(color1.getGreen() - color2.getGreen());
                int blueDiff = Math.abs(color1.getBlue() - color2.getBlue());

                int threshold = 30;

                if (redDiff > threshold || greenDiff > threshold || blueDiff > threshold) {
                    diffCount++;
                }
            }
        }
        return (double) diffCount / numPixels * 100.0;
    }
}
