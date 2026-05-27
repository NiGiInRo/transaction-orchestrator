package com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest;

import com.nicolasdev.transactionorchestrator.domain.model.Customer;
import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.ports.in.CreateTransactionUseCase;
import com.nicolasdev.transactionorchestrator.domain.ports.in.GetTransactionUseCase;
import com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest.dto.request.TransactionRequest;
import com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest.dto.response.ApiResponse;
import com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest.dto.response.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionUseCase getTransactionUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                 GetTransactionUseCase getTransactionUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.getTransactionUseCase = getTransactionUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = toDomain(request);
        Transaction result = createTransactionUseCase.create(transaction);
        return ApiResponse.ok(toResponse(result));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TransactionResponse> getById(@PathVariable UUID id) {
        Transaction result = getTransactionUseCase.getById(id);
        return ApiResponse.ok(toResponse(result));
    }

    private Transaction toDomain(TransactionRequest request) {
        return Transaction.builder()
                .clientTransactionId(request.getClientTransactionId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .country(request.getCountry())
                .paymentMethodId(request.getPaymentMethodId())
                .webhookUrl(request.getWebhookUrl())
                .redirectUrl(request.getRedirectUrl())
                .description(request.getDescription())
                .expirationTime(request.getExpirationTime())
                .customer(toCustomerDomain(request.getCustomer()))
                .build();
    }

    private Customer toCustomerDomain(
            com.nicolasdev.transactionorchestrator.infrastructure.adapter.in.rest.dto.request.CustomerRequest request) {
        return Customer.builder()
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .countryCode(request.getCountryCode())
                .phone(request.getPhone())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .secondLastName(request.getSecondLastName())
                .build();
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .processedAt(transaction.getProcessedAt())
                .clientTransactionId(transaction.getClientTransactionId())
                .paymentMethodId(transaction.getPaymentMethodId())
                .currency(transaction.getCurrency())
                .country(transaction.getCountry())
                .description(transaction.getDescription())
                .build();
    }
}
