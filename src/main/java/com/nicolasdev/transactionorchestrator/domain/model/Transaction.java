package com.nicolasdev.transactionorchestrator.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private UUID id;
    private String clientTransactionId;
    private Long amount;
    private String currency;
    private String country;
    private String paymentMethodId;
    private Customer customer;
    private String webhookUrl;
    private String redirectUrl;
    private String description;
    private LocalDateTime expirationTime;
    private TransactionStatus status;
    private LocalDateTime processedAt;
}
