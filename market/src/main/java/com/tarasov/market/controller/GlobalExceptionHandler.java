package com.tarasov.market.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler( { NoSuchElementException.class, IllegalStateException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleNotFoundEntityException(Exception e) {
        LOGGER.error("Not Found Exception", e);
        return Mono.just("error");
    }
}
