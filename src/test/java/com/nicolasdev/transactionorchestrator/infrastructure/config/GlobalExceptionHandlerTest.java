package com.nicolasdev.transactionorchestrator.infrastructure.config;

import com.nicolasdev.transactionorchestrator.domain.exception.TransactionNotFoundException;
import com.nicolasdev.transactionorchestrator.domain.exception.ValidationException;
import com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest.dto.response.ApiResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidation_shouldReturnErrorWithCode() {
        ValidationException ex = new ValidationException("002", "Formato de email inválido");

        ApiResponse<Void> response = handler.handleValidation(ex);

        assertThat(response.getCode()).isEqualTo("002");
        assertThat(response.getMessage()).isEqualTo("Formato de email inválido");
    }

    @Test
    void handleNotFound_shouldReturn404Code() {
        TransactionNotFoundException ex = new TransactionNotFoundException(UUID.randomUUID());

        ApiResponse<Void> response = handler.handleNotFound(ex);

        assertThat(response.getCode()).isEqualTo("404");
    }

    @Test
    void handleGeneric_shouldReturn500WithoutDetails() {
        Exception ex = new RuntimeException("Error interno detallado");

        ApiResponse<Void> response = handler.handleGeneric(ex);

        assertThat(response.getCode()).isEqualTo("500");
        assertThat(response.getMessage()).isEqualTo("Error interno del servidor");
        assertThat(response.getMessage()).doesNotContain("detallado");
    }
}
