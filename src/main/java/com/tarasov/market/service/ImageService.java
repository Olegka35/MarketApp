package com.tarasov.market.service;

import org.springframework.web.multipart.MultipartFile;


public interface ImageService {

    void uploadImage(MultipartFile image);

}
