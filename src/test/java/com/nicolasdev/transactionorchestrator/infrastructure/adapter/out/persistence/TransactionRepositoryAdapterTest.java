package com.nicolasdev.transactionorchestrator.infrastructure.adapter.out.persistence;

import com.nicolasdev.transactionorchestrator.domain.model.Customer;
import com.nicolasdev.transactionorchestrator.domain.model.DocumentType;
import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.model.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TransactionRepositoryAdapter.class, TransactionMapper.class})
class TransactionRepositoryAdapterTest {

    @Autowired
    private TransactionRepositoryAdapter adapter;

    @Autowired
    private SpringPaymentMethodRepository paymentMethodRepository;

    @BeforeEach
    void setUp() {
        paymentMethodRepository.save(PaymentMethodJpaEntity.builder()
                .id("PSE")
                .name("PSE — Pagos Seguros en Línea")
                .enabled(true)
                .build());
    }

    @Test
    void save_shouldPersistTransactionAndReturnItWithId() {
        Transaction transaction = buildValidTransaction();

        Transaction saved = adapter.save(transaction);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getClientTransactionId()).isEqualTo("CLIENT-001");
        assertThat(saved.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(saved.getCustomer().getEmail()).isEqualTo("juan@example.com");
    }

    @Test
    void findById_shouldReturnTransaction_whenExists() {
        Transaction saved = adapter.save(buildValidTransaction());

        Optional<Transaction> found = adapter.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getPaymentMethodId()).isEqualTo("PSE");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Transaction> found = adapter.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    private Transaction buildValidTransaction() {
        Customer customer = Customer.builder()
                .documentType(DocumentType.CC)
                .documentNumber("123456789")
                .countryCode("+57")
                .phone("3001234567")
                .email("juan@example.com")
                .firstName("Juan")
                .lastName("Pérez")
                .build();

        return Transaction.builder()
                .id(UUID.randomUUID())
                .clientTransactionId("CLIENT-001")
                .amount(1_000_000L)
                .currency("COP")
                .country("CO")
                .paymentMethodId("PSE")
                .webhookUrl("https://example.com/webhook")
                .redirectUrl("https://example.com/redirect")
                .customer(customer)
                .status(TransactionStatus.PENDING)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
