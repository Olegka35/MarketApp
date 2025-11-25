package com.tarasov.market.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler( { NoSuchElementException.class, IllegalStateException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleNotFoundEntityException() {
        return Mono.just("error");
    }

    @ExceptionHandler( { Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<String> handleInternalErrorException() {
        return Mono.just("error");
    }
}
