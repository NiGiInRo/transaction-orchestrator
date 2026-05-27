package com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TransactionResponse {

    private final UUID transactionId;
    private final LocalDateTime processedAt;
    private final String clientTransactionId;
    private final String paymentMethodId;
    private final String currency;
    private final String country;
    private final String description;
}
