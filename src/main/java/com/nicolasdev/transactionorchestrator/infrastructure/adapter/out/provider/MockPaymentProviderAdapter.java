package com.nicolasdev.transactionorchestrator.infrastructure.adapter.out.provider;

import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.model.TransactionStatus;
import com.nicolasdev.transactionorchestrator.domain.ports.out.PaymentProviderPort;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentProviderAdapter implements PaymentProviderPort {

    @Override
    public Transaction dispatch(Transaction transaction) {
        transaction.setStatus(TransactionStatus.APPROVED);
        return transaction;
    }
}
