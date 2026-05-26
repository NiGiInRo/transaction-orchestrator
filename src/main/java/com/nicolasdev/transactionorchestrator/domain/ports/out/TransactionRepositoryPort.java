package com.nicolasdev.transactionorchestrator.domain.ports.out;

import com.nicolasdev.transactionorchestrator.domain.model.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);
}
