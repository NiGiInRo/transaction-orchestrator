package com.nicolasdev.transactionorchestrator.infrastructure.adapter.out.persistence;

import com.nicolasdev.transactionorchestrator.domain.model.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    private DocumentType documentType;

    @Column(name = "document_number", nullable = false, length = 30)
    private String documentNumber;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "middle_name", length = 80)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(name = "second_last_name", length = 80)
    private String secondLastName;
}
