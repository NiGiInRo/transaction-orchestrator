package com.nicolasdev.transactionorchestrator.infrastructure.config;

import com.nicolasdev.transactionorchestrator.domain.exception.TransactionNotFoundException;
import com.nicolasdev.transactionorchestrator.domain.exception.ValidationException;
import com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(ValidationException ex) {
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Campo inválido");
        return ApiResponse.error("001", message);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFound(TransactionNotFoundException ex) {
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneric(Exception ex) {
        return ApiResponse.error("500", "Error interno del servidor");
    }
}
