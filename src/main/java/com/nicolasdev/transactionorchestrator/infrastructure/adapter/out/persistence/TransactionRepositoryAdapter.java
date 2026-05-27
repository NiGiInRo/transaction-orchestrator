package com.nicolasdev.transactionorchestrator.infrastructure.adapter.out.persistence;

import com.nicolasdev.transactionorchestrator.domain.exception.ValidationException;
import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.ports.out.TransactionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final SpringTransactionRepository transactionRepository;
    private final SpringPaymentMethodRepository paymentMethodRepository;
    private final TransactionMapper mapper;

    public TransactionRepositoryAdapter(SpringTransactionRepository transactionRepository,
                                        SpringPaymentMethodRepository paymentMethodRepository,
                                        TransactionMapper mapper) {
        this.transactionRepository = transactionRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        PaymentMethodJpaEntity paymentMethod = paymentMethodRepository
                .findById(transaction.getPaymentMethodId())
                .orElseThrow(() -> new ValidationException(
                        "008", "Método de pago no soportado: " + transaction.getPaymentMethodId()));

        CustomerJpaEntity customerJpa = mapper.toCustomerJpaEntity(transaction.getCustomer());
        TransactionJpaEntity entity = mapper.toJpaEntity(transaction, customerJpa, paymentMethod);
        TransactionJpaEntity saved = transactionRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return transactionRepository.findById(id)
                .map(mapper::toDomain);
    }
}
