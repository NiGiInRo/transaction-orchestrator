package com.nicolasdev.transactionorchestrator.application.usecase;

import com.nicolasdev.transactionorchestrator.domain.exception.ValidationException;
import com.nicolasdev.transactionorchestrator.domain.model.Transaction;
import com.nicolasdev.transactionorchestrator.domain.model.TransactionStatus;
import com.nicolasdev.transactionorchestrator.domain.ports.in.CreateTransactionUseCase;
import com.nicolasdev.transactionorchestrator.domain.ports.out.PaymentProviderPort;
import com.nicolasdev.transactionorchestrator.domain.ports.out.TransactionRepositoryPort;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class CreateTransactionUseCaseImpl implements CreateTransactionUseCase {

    private final TransactionRepositoryPort repositoryPort;
    private final PaymentProviderPort providerPort;

    public CreateTransactionUseCaseImpl(TransactionRepositoryPort repositoryPort,
                                        PaymentProviderPort providerPort) {
        this.repositoryPort = repositoryPort;
        this.providerPort = providerPort;
    }

    @Override
    public Transaction create(Transaction transaction) {
        validate(transaction);
        transaction.setId(UUID.randomUUID());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setProcessedAt(LocalDateTime.now());
        Transaction saved = repositoryPort.save(transaction);
        return providerPort.dispatch(saved);
    }

    private void validate(Transaction transaction) {
        requireNonBlank(transaction.getClientTransactionId(), "client_transaction_id");
        requireNonNull(transaction.getAmount(), "amount");
        requireNonBlank(transaction.getCurrency(), "currency");
        requireNonBlank(transaction.getCountry(), "country");
        requireNonBlank(transaction.getPaymentMethodId(), "payment_method_id");
        requireNonBlank(transaction.getWebhookUrl(), "webhook_url");
        requireNonBlank(transaction.getRedirectUrl(), "redirect_url");
        if (transaction.getCustomer() == null) {
            throw new ValidationException("001", "customer es requerido");
        }
        requireNonBlank(transaction.getCustomer().getEmail(), "customer.email");
        validateEmail(transaction.getCustomer().getEmail());
        validateCurrency(transaction.getCurrency());
        validateCountry(transaction.getCountry());
        validateAmount(transaction.getAmount());
        validateUrl(transaction.getWebhookUrl(), "006", "webhook_url inválida");
        validateUrl(transaction.getRedirectUrl(), "007", "redirect_url inválida");
    }

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("001", fieldName + " es requerido");
        }
    }

    private void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException("001", fieldName + " es requerido");
        }
    }

    private void validateEmail(String email) {
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ValidationException("002", "Formato de email inválido");
        }
    }

    private void validateCurrency(String currency) {
        try {
            Currency.getInstance(currency);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("003", "Código de moneda inválido (ISO 4217): " + currency);
        }
    }

    private void validateCountry(String country) {
        Set<String> validCountries = Set.of(Locale.getISOCountries());
        if (!validCountries.contains(country)) {
            throw new ValidationException("004", "Código de país inválido (ISO 3166-1): " + country);
        }
    }

    private void validateAmount(Long amount) {
        if (amount <= 0) {
            throw new ValidationException("005", "El monto debe ser mayor a cero");
        }
    }

    private void validateUrl(String url, String code, String message) {
        try {
            URI.create(url).toURL();
        } catch (Exception e) {
            throw new ValidationException(code, message);
        }
    }
}
