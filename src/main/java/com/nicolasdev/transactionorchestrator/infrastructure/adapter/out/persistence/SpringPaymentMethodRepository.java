package com.nicolasdev.transactionorchestrator.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringPaymentMethodRepository extends JpaRepository<PaymentMethodJpaEntity, String> {
}
