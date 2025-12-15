package com.tarasov.market.controller;


import com.tarasov.market.model.exception.PaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler( { NoSuchElementException.class, IllegalStateException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<Rendering> handleNotFoundEntityException(Exception e) {
        LOGGER.error("Not Found Exception", e);
        return Mono.just(Rendering.view("error").build());
    }

    @ExceptionHandler( { PaymentException.class })
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public Mono<Rendering> handlePaymentException(Exception e) {
        LOGGER.error("Payment error", e);
        return Mono.just(Rendering.view("error").build());
    }

    @ExceptionHandler( { HandlerMethodValidationException.class, ServerWebInputException.class } )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<Rendering> handleValidationFailedException(Exception e) {
        LOGGER.error("Failed validation", e);
        return Mono.just(Rendering.view("error").build());
    }

    @ExceptionHandler( { Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Rendering> handleInternalExceptions(Exception e) {
        LOGGER.error("Internal server error", e);
        return Mono.just(Rendering.view("error").build());
    }
}
