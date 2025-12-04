package com.tarasov.payment.controller;

import com.tarasov.payment.model.UnsufficientBalanceException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleValidationFailedException(Exception e) {
        return Mono.just(e.getMessage());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, List<String>>> handleWebExchangeBindException(WebExchangeBindException exception) {
        Map<String, List<String>> validationErrors = exception.getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                DefaultMessageSourceResolvable::getDefaultMessage,
                                Collectors.toList()
                        )
                ));

        return ResponseEntity.badRequest().body(validationErrors);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleNonExistingElementException(Exception e) {
        return Mono.just(e.getMessage());
    }

    @ExceptionHandler(UnsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public Mono<String> handleUnsufficientBalanceException(Exception e) {
        return Mono.just(e.getMessage());
    }
}
