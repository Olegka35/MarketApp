package com.tarasov.market.service;


import com.tarasov.market.service.impl.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        FilePart image
                = new TestFilePart("image", "test.png", "test content".getBytes());

        imageService.uploadImage(image).block();

        Path expectedPath = tempDir.resolve("test.png");

        assertTrue(Files.exists(expectedPath));
        assertEquals("test content", Files.readString(expectedPath));
    }


    private static class TestFilePart implements FilePart {

        private final String name;
        private final String fileName;
        private final Flux<DataBuffer> data;

        public TestFilePart(String name, String fileName, byte[] data) {
            this.name = name;
            this.fileName = fileName;
            this.data = DataBufferUtils.read(new ByteArrayResource(data), new DefaultDataBufferFactory(), 1024);
        }

        @Override
        public String filename() {
            return fileName;
        }

        @Override
        public Mono<Void> transferTo(Path dest) {
            return DataBufferUtils.write(data, dest);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public HttpHeaders headers() {
            return new HttpHeaders();
        }

        @Override
        public Flux<DataBuffer> content() {
            return data;
        }
    }
}
