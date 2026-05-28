package com.nicolasdev.transactionorchestrator.infrastructure.adapter.out.provider;

import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.model.TransactionStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MockPaymentProviderAdapterTest {

    private final MockPaymentProviderAdapter adapter = new MockPaymentProviderAdapter();

    @Test
    void dispatch_shouldSetStatusToApproved() {
        Transaction transaction = Transaction.builder()
                .status(TransactionStatus.PENDING)
                .build();

        Transaction result = adapter.dispatch(transaction);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    }
}
