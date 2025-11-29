package com.tarasov.market.model;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

public class TestFilePart implements FilePart {

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
