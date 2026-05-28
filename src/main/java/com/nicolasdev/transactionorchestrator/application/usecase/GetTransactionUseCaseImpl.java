package com.nicolasdev.transactionorchestrator.application.usecase;

import com.nicolasdev.transactionorchestrator.domain.exception.TransactionNotFoundException;
import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.ports.in.GetTransactionUseCase;
import com.nicolasdev.transactionorchestrator.domain.ports.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetTransactionUseCaseImpl implements GetTransactionUseCase {

    private final TransactionRepositoryPort repositoryPort;

    public GetTransactionUseCaseImpl(TransactionRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Transaction getById(UUID id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }
}
