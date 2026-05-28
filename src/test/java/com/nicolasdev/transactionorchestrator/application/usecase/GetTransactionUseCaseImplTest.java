package com.nicolasdev.transactionorchestrator.application.usecase;

import com.nicolasdev.transactionorchestrator.domain.exception.TransactionNotFoundException;
import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.ports.out.TransactionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTransactionUseCaseImplTest {

    @Mock
    private TransactionRepositoryPort repositoryPort;

    @InjectMocks
    private GetTransactionUseCaseImpl useCase;

    @Test
    void getById_shouldReturnTransaction_whenExists() {
        UUID id = UUID.randomUUID();
        Transaction transaction = Transaction.builder().id(id).build();
        when(repositoryPort.findById(id)).thenReturn(Optional.of(transaction));

        Transaction result = useCase.getById(id);

        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void getById_shouldThrowTransactionNotFoundException_whenNotExists() {
        UUID id = UUID.randomUUID();
        when(repositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getById(id))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
