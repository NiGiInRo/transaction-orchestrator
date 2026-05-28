package com.nicolasdev.transactionorchestrator.domain.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private UUID id;
    private DocumentType documentType;
    private String documentNumber;
    private String countryCode;
    private String phone;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
}
