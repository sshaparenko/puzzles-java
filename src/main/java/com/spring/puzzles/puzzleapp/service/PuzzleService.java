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
    private String dirPath = "src/main/resources/static/images/";

    public void saveImage(MultipartFile image) {
        try{
            InputStream stream = image.getInputStream();
            BufferedImage bufferedImage = ImageIO.read(stream);
            File file = new File(dirPath + image.getOriginalFilename());
            ImageIO.write(bufferedImage, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImage(String filename, String dirPath) throws IOException {
        File file = new File(dirPath, filename);
        image = ImageIO.read(file);
    }

    private BufferedImage[] split(int rows, int cols) {
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

    public String savePuzzles(int rows, int cols, String filename, String dirPath) throws IOException {
        BufferedImage[] puzzles = split(rows, cols);

        Map<Integer, String> puzzleMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder puzzlesPathBuilder = new StringBuilder();
        String noExtensionName = filename.split("\\.")[0];
        puzzlesPathBuilder.append(dirPath).append("puzzles/").append(noExtensionName);

        File dir = new File(puzzlesPathBuilder.toString());
        if (!dir.exists()) dir.mkdir();

        for (int i = 0; i < puzzles.length; i++) {
            File file = new File(puzzlesPathBuilder.toString(), i + ".png");
            ImageIO.write(puzzles[i], "png", file);
            puzzleMap.put(i, file.getPath());
        }
        return mapper.writeValueAsString(puzzleMap);
    }
}