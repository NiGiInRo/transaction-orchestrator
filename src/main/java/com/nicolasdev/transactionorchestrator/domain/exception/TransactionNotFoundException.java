package com.nicolasdev.transactionorchestrator.domain.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TransactionNotFoundException extends RuntimeException {

    private final String code = "404";

    public TransactionNotFoundException(UUID id) {
        super("Transacción no encontrada con id: " + id);
    }
}
