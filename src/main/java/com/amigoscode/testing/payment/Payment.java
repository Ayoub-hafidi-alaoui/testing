package com.amigoscode.testing.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Payment {
    private Long paymentId;
    private UUID customerId;
    private BigDecimal amount;
    private Currency currency;
    private String source;
    private String description;
}
