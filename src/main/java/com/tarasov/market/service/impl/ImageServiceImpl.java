package com.tarasov.market.service.impl;

import com.tarasov.market.model.ImageUploadException;
import com.tarasov.market.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class ImageServiceImpl implements ImageService {

    @Value("${offering.images-directory}")
    private String imageUploadDirectory;

    @Override
    public void uploadImage(MultipartFile image) {
        try {
            Path uploadDir = Paths.get(imageUploadDirectory);
            Path filePath = uploadDir.resolve(image.getOriginalFilename());
            image.transferTo(filePath.toFile());
        } catch (Exception exception) {
            throw new ImageUploadException(exception.getMessage());
        }
    }
}
