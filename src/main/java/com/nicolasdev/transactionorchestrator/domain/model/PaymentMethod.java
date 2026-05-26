package com.nicolasdev.transactionorchestrator.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

    private String id;
    private String name;
    private boolean enabled;
}
