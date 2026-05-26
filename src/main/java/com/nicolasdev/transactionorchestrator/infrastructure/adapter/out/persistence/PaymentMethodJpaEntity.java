package com.nicolasdev.transactionorchestrator.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;
}
