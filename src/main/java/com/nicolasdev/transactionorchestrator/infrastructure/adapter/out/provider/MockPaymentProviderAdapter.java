package com.nicolasdev.transactionorchestrator.infrastructure.adapter.out.provider;

import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.model.TransactionStatus;
import com.nicolasdev.transactionorchestrator.domain.ports.out.PaymentProviderPort;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentProviderAdapter implements PaymentProviderPort {

    @Override
    public Transaction dispatch(Transaction transaction) {
        // Simula la respuesta exitosa de un proveedor de pagos externo.
        // En producción, aquí iría la llamada HTTP al proveedor real (PSE, Stripe, etc.)
        transaction.setStatus(TransactionStatus.APPROVED);
        return transaction;
    }
}
