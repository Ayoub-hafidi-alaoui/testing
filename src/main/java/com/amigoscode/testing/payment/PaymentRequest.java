package com.amigoscode.testing.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 */
@Data
public class PaymentRequest {
    private Payment payment;

    public PaymentRequest(@JsonProperty("payment")Payment payment) {
        this.payment = payment;
    }
}
