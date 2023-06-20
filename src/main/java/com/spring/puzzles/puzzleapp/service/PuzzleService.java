package com.spring.puzzles.puzzleapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Data
public class PuzzleService {
    private BufferedImage image;
    private String dirPath = "src/test/resources/static/images/";

    public void saveImage(MultipartFile image) throws IOException {
        InputStream stream = image.getInputStream();
        BufferedImage bufferedImage = ImageIO.read(stream);
        File file = new File(dirPath + image.getOriginalFilename());
        ImageIO.write(bufferedImage, "jpg", file);
    }

    public void setImage(String path) throws IOException {
        File file = new File(path);
        image = ImageIO.read(file);
    }

    public BufferedImage[] split(int rows, int cols) {
        int puzzleWidth = image.getWidth() / cols;
        int puzzleHeight = image.getHeight() / rows;
        int count = 0;

        BufferedImage[] puzzles = new BufferedImage[rows * cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                puzzles[count] = image.getSubimage(puzzleWidth * j, puzzleHeight * i, puzzleWidth, puzzleHeight);
                count++;
            }
        }
        return puzzles;
    }

    public String savePuzzles(int rows, int cols, String filename) throws IOException {
        BufferedImage[] puzzles = split(rows, cols);
        Map<Integer, String> puzzleMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        String filePath = dirPath + "puzzles/" + filename.split("\\.")[0];

        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdir();

        for (int i = 0; i < puzzles.length; i++) {
            File file = new File(filePath, i + ".jpg");
            ImageIO.write(puzzles[i], "jpg", file);
            puzzleMap.put(i, file.getPath());
        }
        return mapper.writeValueAsString(puzzleMap);
    }
}
