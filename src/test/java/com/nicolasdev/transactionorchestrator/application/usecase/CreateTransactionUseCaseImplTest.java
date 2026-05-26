package com.nicolasdev.transactionorchestrator.application.usecase;

import com.nicolasdev.transactionorchestrator.domain.exception.ValidationException;
import com.nicolasdev.transactionorchestrator.domain.model.Customer;
import com.nicolasdev.transactionorchestrator.domain.model.DocumentType;
import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.model.TransactionStatus;
import com.nicolasdev.transactionorchestrator.domain.ports.out.PaymentProviderPort;
import com.nicolasdev.transactionorchestrator.domain.ports.out.TransactionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseImplTest {

    @Mock
    private TransactionRepositoryPort repositoryPort;

    @Mock
    private PaymentProviderPort providerPort;

    @InjectMocks
    private CreateTransactionUseCaseImpl useCase;

    // ── Camino feliz ────────────────────────────────────────────────────────

    @Test
    void create_shouldReturnApprovedTransaction_whenInputIsValid() {
        Transaction input = buildValidTransaction();

        Transaction saved = buildValidTransaction();
        saved.setId(UUID.randomUUID());

        Transaction dispatched = buildValidTransaction();
        dispatched.setStatus(TransactionStatus.APPROVED);

        when(repositoryPort.save(any())).thenReturn(saved);
        when(providerPort.dispatch(any())).thenReturn(dispatched);

        Transaction result = useCase.create(input);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.APPROVED);
        verify(repositoryPort).save(any());
        verify(providerPort).dispatch(any());
    }

    // ── Casos de error ──────────────────────────────────────────────────────

    @Test
    void create_shouldThrow002_whenEmailIsInvalid() {
        Transaction transaction = buildValidTransaction();
        transaction.getCustomer().setEmail("not-an-email");

        assertThatThrownBy(() -> useCase.create(transaction))
                .isInstanceOf(ValidationException.class)
                .extracting("code").isEqualTo("002");
    }

    @Test
    void create_shouldThrow005_whenAmountIsNegative() {
        Transaction transaction = buildValidTransaction();
        transaction.setAmount(-100L);

        assertThatThrownBy(() -> useCase.create(transaction))
                .isInstanceOf(ValidationException.class)
                .extracting("code").isEqualTo("005");
    }

    @Test
    void create_shouldThrow003_whenCurrencyIsInvalid() {
        Transaction transaction = buildValidTransaction();
        transaction.setCurrency("INVALID");

        assertThatThrownBy(() -> useCase.create(transaction))
                .isInstanceOf(ValidationException.class)
                .extracting("code").isEqualTo("003");
    }

    // ── Builder de datos de prueba ──────────────────────────────────────────

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
                .clientTransactionId("CLIENT-001")
                .amount(1_000_000L)
                .currency("COP")
                .country("CO")
                .paymentMethodId("PSE")
                .webhookUrl("https://example.com/webhook")
                .redirectUrl("https://example.com/redirect")
                .customer(customer)
                .build();
    }
}
