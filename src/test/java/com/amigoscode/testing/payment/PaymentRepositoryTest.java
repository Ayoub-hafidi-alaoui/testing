package com.amigoscode.testing.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;


    @Test
    void itShouldInsertPayment() {
        //given
        Payment payment = new Payment(1L, UUID.randomUUID(), new BigDecimal(12), Currency.EUR, "card123", "donation");

        //when
        underTest.save(payment);

        //then
        Optional<Payment> paymentOptional = underTest.findById(payment.getPaymentId());
        assertThat(paymentOptional)
                .isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p).isEqualToComparingFieldByField(payment);
                });
    }
}