package com.tarasov.market.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;


public interface ImageService {

    Mono<Void> uploadImage(FilePart image);

}
