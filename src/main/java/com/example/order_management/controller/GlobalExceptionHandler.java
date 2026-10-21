package com.example.order_management.controller;

import com.example.order_management.exception.InsufficientStockException;
import com.example.order_management.exception.ProductNotFoundException;
import com.example.order_management.service.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleOrderNotFound(
            OrderNotFoundException exception) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleProductNotFound(
            ProductNotFoundException exception) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleInsufficientStock(
            InsufficientStockException exception) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }
}
