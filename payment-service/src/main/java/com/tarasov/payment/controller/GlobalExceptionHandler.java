package com.tarasov.payment.controller;

import com.tarasov.payment.model.UnsufficientBalanceException;
import com.tarasov.payment.model.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @ExceptionHandler(value = ConstraintViolationException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> handleValidationFailedException(Exception e) {
        return Mono.just(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = WebExchangeBindException.class, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @ExceptionHandler(value = NoSuchElementException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ErrorResponse> handleNonExistingElementException(Exception e) {
        return Mono.just(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = UnsufficientBalanceException.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public Mono<ErrorResponse> handleUnsufficientBalanceException(Exception e) {
        return Mono.just(new ErrorResponse(e.getMessage()));
    }
}
