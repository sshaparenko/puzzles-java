package com.spring.puzzles.puzzleapp.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class PuzzleServiceTest {
    private PuzzleService puzzleService;
    private final String dirPath = "src/test/resources/images/";
    private final String imageName = "blank.jpg";

    @BeforeEach
    void initUseCase() {
        puzzleService = new PuzzleService();
    }

    @Test
    void saveImage() throws IOException {
        Path imagePath = Paths.get("src/test/resources/images/blank.jpg");

        byte[] imageBytes = Files.readAllBytes(imagePath);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "image.jpg",
                "image.jpg",
                "image/jpeg",
                imageBytes
        );

        puzzleService.setDirPath(dirPath);

        try {
            puzzleService.saveImage(multipartFile);

            Path savedImagePath = Paths.get(dirPath, "image.jpg");
            Assertions.assertThat(Files.exists(savedImagePath)).isTrue();

            BufferedImage savedImage = ImageIO.read(savedImagePath.toFile());
            Assertions.assertThat(savedImage).isNotNull();
            Files.deleteIfExists(savedImagePath);
        } catch (IOException e) {
            e.printStackTrace();
            Assertions.fail("Failed to save the image");
        }
    }

    @Test
    void setImage() {
        try {
            puzzleService.setImage(imageName, dirPath);
            Assertions.assertThat(puzzleService.getImage()).isNotNull();
        } catch (IOException e) {
            e.printStackTrace();
            Assertions.fail("Failed to set the image");
        }
    }

    @Test
    void savePuzzles() {
        try {
            puzzleService.setImage(imageName, dirPath);
            puzzleService.savePuzzles(2,2, imageName, dirPath);
            String puzzlesPath = new StringBuilder()
                    .append(dirPath).append("puzzles/")
                    .append(imageName.split("\\.")[0])
                    .toString();

            File file = new File(puzzlesPath);
            File[] files = file.listFiles();
            Assertions.assertThat(file.exists()).isTrue();
            Assertions.assertThat(files.length).isEqualTo(4);
            for (File puzzleFile: files) Files.deleteIfExists(Path.of(puzzleFile.getPath()));
            Files.deleteIfExists(Path.of(puzzlesPath));
        } catch (IOException e) {
            e.printStackTrace();
            Assertions.fail("Failed to save puzzles");
        }
    }

    @Test
    void getImage() {
        BufferedImage testImage = new BufferedImage(1,1, BufferedImage.TYPE_INT_RGB);
        puzzleService.setImage(testImage);
        Assertions.assertThat(puzzleService.getImage()).isEqualTo(testImage);
    }
}