package com.nicolasdev.transactionorchestrator.domain.ports.in;

import com.nicolasdev.transactionorchestrator.domain.model.Transaction;

public interface CreateTransactionUseCase {

    Transaction create(Transaction transaction);
}
