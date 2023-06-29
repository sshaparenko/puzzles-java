package com.spring.puzzles.puzzleapp.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Getter
@Setter
@Slf4j
public class PuzzleAutoSolve {
    private String puzzlesDir = "src/main/resources/static/images/puzzles/";
    private String imagesDir = "src/main/resources/static/images/";
    /**
     * @param originalImageName name of the original image
     * @return InputStream of bytes of the result image
     * @throws IOException
     */
    public InputStream solve(String originalImageName) throws IOException {
        log.info("Solving process has started...");

        LinkedList<BufferedImage> filesList = getFiles(puzzlesDir + originalImageName.split("\\.")[0]);

        if (filesList.size() == 0) {
            log.warn("No files were found! Try to generate them on /api/v1/puzzle");
            throw new IOException();
        }

        File origialImageFile = new File(imagesDir + originalImageName);
        BufferedImage originalImage = ImageIO.read(origialImageFile);

        int puzzleHeight = filesList.get(0).getHeight();
        int puzzleWidth = filesList.get(0).getWidth();
        int rows = originalImage.getHeight() / puzzleHeight;
        int columns = originalImage.getWidth() / puzzleWidth;

        Map<Integer, BufferedImage> resultMap = new HashMap<>();
        Iterator<BufferedImage> iterator = filesList.iterator();
        while (iterator.hasNext()) {
            int count = 0;
            int min_count = 0;
            double min_differance = 100.0;
            BufferedImage puzzle = iterator.next();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    double current_differance = getDifference(puzzle, originalImage, i, j);
                    if (min_differance > current_differance) {
                        min_differance = current_differance;
                        min_count = count;
                    }
                    count++;
                }
            }
            resultMap.put(min_count, puzzle);
            iterator.remove();
        }
        BufferedImage resultImage = createImage(originalImage.getWidth(), originalImage.getHeight(), resultMap);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resultImage, "jpg", outputStream);
        outputStream.flush();
        byte[] imageBytes = outputStream.toByteArray();
        outputStream.close();

        log.info("Solving process has finished...");
        return new ByteArrayInputStream(imageBytes);
    }

    /**
     * Generates the result image from puzzles dictionary (index, puzzle)
     *
     * @param width    total width
     * @param height   total height
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
     * Access images of puzzles in the specified dir and put them into List.
     *
     * @param dirPath path do the directory, where all the puzzles stored
     * @return list of puzzles of type BufferedImage
     */
    private LinkedList<BufferedImage> getFiles(String dirPath) {
        log.info("Starting the read of files...");
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

    /**
     * Calculates the difference between images (puzzle and area of original image). This difference is used in the solve() method
     * in order to find the position of the puzzle.
     *
     * @param imageToCompare generated image, that should be compared with the original
     * @param original       original image, that should be compared with the generated one
     * @return percentage of difference
     */
    private double getDifference(BufferedImage imageToCompare, BufferedImage original, int row, int col) {
        int width1 = imageToCompare.getWidth();
        int height1 = imageToCompare.getHeight();

        int numPixels = width1 * height1;
        int diffCount = 0;

        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                int rgb1 = imageToCompare.getRGB(x, y);
                int rgb2 = original.getRGB(col * width1 + x, row * height1 + y);

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
