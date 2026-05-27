package com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionRequest {

    @NotBlank
    private String clientTransactionId;

    @NotNull
    @Positive
    private Long amount;

    @NotBlank
    private String currency;

    @NotBlank
    private String country;

    @NotBlank
    private String paymentMethodId;

    @NotBlank
    private String webhookUrl;

    @NotBlank
    private String redirectUrl;

    @NotNull
    @Valid
    private CustomerRequest customer;

    private String description;

    private LocalDateTime expirationTime;
}
