package com.nicolasdev.transactionorchestrator.domain.ports.out;

import com.nicolasdev.transactionorchestrator.domain.model.Transaction;

public interface PaymentProviderPort {

    Transaction dispatch(Transaction transaction);
}
