package com.tarasov.market.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler( { EntityNotFoundException.class, NoResultException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundEntityException() {
        return "error";
    }
}
