package com.tarasov.market.service.impl;

import com.tarasov.market.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class ImageServiceImpl implements ImageService {

    @Value("${offering.images-directory}")
    private String imageUploadDirectory;

    @Override
    public Mono<Void> uploadImage(FilePart image) {
        Path uploadDir = Paths.get(imageUploadDirectory);
        Path filePath = uploadDir.resolve("image").resolve(image.filename());
        return image.transferTo(filePath.toFile());
    }
}
