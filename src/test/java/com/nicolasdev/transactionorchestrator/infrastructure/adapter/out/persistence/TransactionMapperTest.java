package com.nicolasdev.transactionorchestrator.infrastructure.adapter.out.persistence;

import com.nicolasdev.transactionorchestrator.domain.model.Customer;
import com.nicolasdev.transactionorchestrator.domain.model.DocumentType;
import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.model.TransactionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private final TransactionMapper mapper = new TransactionMapper();

    @Test
    void toCustomerJpaEntity_shouldMapAllFields() {
        Customer customer = buildCustomer();

        CustomerJpaEntity result = mapper.toCustomerJpaEntity(customer);

        assertThat(result.getDocumentType()).isEqualTo(customer.getDocumentType());
        assertThat(result.getDocumentNumber()).isEqualTo(customer.getDocumentNumber());
        assertThat(result.getEmail()).isEqualTo(customer.getEmail());
        assertThat(result.getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(result.getLastName()).isEqualTo(customer.getLastName());
        assertThat(result.getPhone()).isEqualTo(customer.getPhone());
        assertThat(result.getCountryCode()).isEqualTo(customer.getCountryCode());
    }

    @Test
    void toJpaEntity_shouldMapAllFields() {
        Transaction transaction = buildTransaction();
        CustomerJpaEntity customerJpa = mapper.toCustomerJpaEntity(transaction.getCustomer());
        PaymentMethodJpaEntity paymentMethodJpa = PaymentMethodJpaEntity.builder()
                .id("PSE").name("PSE").enabled(true).build();

        TransactionJpaEntity result = mapper.toJpaEntity(transaction, customerJpa, paymentMethodJpa);

        assertThat(result.getId()).isEqualTo(transaction.getId());
        assertThat(result.getClientTransactionId()).isEqualTo(transaction.getClientTransactionId());
        assertThat(result.getAmount()).isEqualTo(transaction.getAmount());
        assertThat(result.getCurrency()).isEqualTo(transaction.getCurrency());
        assertThat(result.getCountry()).isEqualTo(transaction.getCountry());
        assertThat(result.getStatus()).isEqualTo(transaction.getStatus());
        assertThat(result.getWebhookUrl()).isEqualTo(transaction.getWebhookUrl());
        assertThat(result.getRedirectUrl()).isEqualTo(transaction.getRedirectUrl());
    }

    @Test
    void toDomain_shouldMapAllFields() {
        CustomerJpaEntity customerJpa = buildCustomerJpa();
        PaymentMethodJpaEntity paymentMethodJpa = PaymentMethodJpaEntity.builder()
                .id("PSE").name("PSE").enabled(true).build();
        TransactionJpaEntity jpa = buildTransactionJpa(customerJpa, paymentMethodJpa);

        Transaction result = mapper.toDomain(jpa);

        assertThat(result.getId()).isEqualTo(jpa.getId());
        assertThat(result.getClientTransactionId()).isEqualTo(jpa.getClientTransactionId());
        assertThat(result.getAmount()).isEqualTo(jpa.getAmount());
        assertThat(result.getCurrency()).isEqualTo(jpa.getCurrency());
        assertThat(result.getPaymentMethodId()).isEqualTo("PSE");
        assertThat(result.getCustomer().getEmail()).isEqualTo(customerJpa.getEmail());
        assertThat(result.getStatus()).isEqualTo(jpa.getStatus());
    }

    private Customer buildCustomer() {
        return Customer.builder()
                .id(UUID.randomUUID())
                .documentType(DocumentType.CC)
                .documentNumber("123456789")
                .countryCode("+57")
                .phone("3001234567")
                .email("juan@example.com")
                .firstName("Juan")
                .lastName("Pérez")
                .build();
    }

    private Transaction buildTransaction() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .clientTransactionId("CLIENT-001")
                .amount(1_000_000L)
                .currency("COP")
                .country("CO")
                .paymentMethodId("PSE")
                .webhookUrl("https://example.com/webhook")
                .redirectUrl("https://example.com/redirect")
                .status(TransactionStatus.PENDING)
                .processedAt(LocalDateTime.now())
                .customer(buildCustomer())
                .build();
    }

    private CustomerJpaEntity buildCustomerJpa() {
        return CustomerJpaEntity.builder()
                .id(UUID.randomUUID())
                .documentType(DocumentType.CC)
                .documentNumber("123456789")
                .countryCode("+57")
                .phone("3001234567")
                .email("juan@example.com")
                .firstName("Juan")
                .lastName("Pérez")
                .build();
    }

    private TransactionJpaEntity buildTransactionJpa(CustomerJpaEntity customer,
                                                      PaymentMethodJpaEntity paymentMethod) {
        return TransactionJpaEntity.builder()
                .id(UUID.randomUUID())
                .clientTransactionId("CLIENT-001")
                .amount(1_000_000L)
                .currency("COP")
                .country("CO")
                .paymentMethod(paymentMethod)
                .customer(customer)
                .webhookUrl("https://example.com/webhook")
                .redirectUrl("https://example.com/redirect")
                .status(TransactionStatus.PENDING)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
