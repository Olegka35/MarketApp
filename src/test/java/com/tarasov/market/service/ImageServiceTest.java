package com.tarasov.market.service;


import com.tarasov.market.service.impl.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    private ImageService imageService;

    @TempDir
    private Path tempDir;

    @BeforeEach
    public void setUp() {
        imageService = new ImageServiceImpl();
        ReflectionTestUtils.setField(imageService, "imageUploadDirectory", tempDir.toString());
    }

    @Test
    public void uploadImageTest() throws IOException {
        MultipartFile image = new MockMultipartFile("image",
                "test.png", "image/png", "test content".getBytes());

        imageService.uploadImage(image);

        Path expectedPath = tempDir.resolve("test.png");

        assertTrue(Files.exists(expectedPath));
        assertEquals("test content", Files.readString(expectedPath));
    }
}
