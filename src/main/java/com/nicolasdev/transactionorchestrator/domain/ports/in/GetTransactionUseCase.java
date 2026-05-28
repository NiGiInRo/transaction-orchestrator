package com.nicolasdev.transactionorchestrator.domain.ports.in;

import com.nicolasdev.transactionorchestrator.domain.model.Transaction;

import java.util.UUID;

public interface GetTransactionUseCase {

    Transaction getById(UUID id);
}
