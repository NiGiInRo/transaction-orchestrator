package com.nicolasdev.transactionorchestrator.infrastructure.adapter.out.persistence;

import com.nicolasdev.transactionorchestrator.domain.model.Customer;
import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionJpaEntity toJpaEntity(Transaction domain,
                                            CustomerJpaEntity customerJpa,
                                            PaymentMethodJpaEntity paymentMethodJpa) {
        return TransactionJpaEntity.builder()
                .id(domain.getId())
                .clientTransactionId(domain.getClientTransactionId())
                .amount(domain.getAmount())
                .currency(domain.getCurrency())
                .country(domain.getCountry())
                .customer(customerJpa)
                .paymentMethod(paymentMethodJpa)
                .webhookUrl(domain.getWebhookUrl())
                .redirectUrl(domain.getRedirectUrl())
                .description(domain.getDescription())
                .expirationTime(domain.getExpirationTime())
                .status(domain.getStatus())
                .processedAt(domain.getProcessedAt())
                .build();
    }

    public CustomerJpaEntity toCustomerJpaEntity(Customer domain) {
        return CustomerJpaEntity.builder()
                .id(domain.getId())
                .documentType(domain.getDocumentType())
                .documentNumber(domain.getDocumentNumber())
                .countryCode(domain.getCountryCode())
                .phone(domain.getPhone())
                .email(domain.getEmail())
                .firstName(domain.getFirstName())
                .middleName(domain.getMiddleName())
                .lastName(domain.getLastName())
                .secondLastName(domain.getSecondLastName())
                .build();
    }

    public Transaction toDomain(TransactionJpaEntity jpa) {
        return Transaction.builder()
                .id(jpa.getId())
                .clientTransactionId(jpa.getClientTransactionId())
                .amount(jpa.getAmount())
                .currency(jpa.getCurrency())
                .country(jpa.getCountry())
                .paymentMethodId(jpa.getPaymentMethod().getId())
                .customer(toCustomerDomain(jpa.getCustomer()))
                .webhookUrl(jpa.getWebhookUrl())
                .redirectUrl(jpa.getRedirectUrl())
                .description(jpa.getDescription())
                .expirationTime(jpa.getExpirationTime())
                .status(jpa.getStatus())
                .processedAt(jpa.getProcessedAt())
                .build();
    }

    private Customer toCustomerDomain(CustomerJpaEntity jpa) {
        return Customer.builder()
                .id(jpa.getId())
                .documentType(jpa.getDocumentType())
                .documentNumber(jpa.getDocumentNumber())
                .countryCode(jpa.getCountryCode())
                .phone(jpa.getPhone())
                .email(jpa.getEmail())
                .firstName(jpa.getFirstName())
                .middleName(jpa.getMiddleName())
                .lastName(jpa.getLastName())
                .secondLastName(jpa.getSecondLastName())
                .build();
    }
}
