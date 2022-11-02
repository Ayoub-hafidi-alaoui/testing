package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CardPaymentCharger cardPaymentCharger;
    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccefully() {
        //given
        UUID customerID = UUID.randomUUID();
        given(customerRepository.findById(customerID)).willReturn(Optional.of(mock(Customer.class)));
        Currency currency = Currency.EUR;
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal(100),
                        currency,
                        "card",
                        "donation"
                )
        );
        given(
                cardPaymentCharger.chargeCard(
                        paymentRequest.getPayment().getSource(),
                        paymentRequest.getPayment().getAmount(),
                        paymentRequest.getPayment().getCurrency(),
                        paymentRequest.getPayment().getDescription()
                )).willReturn(new CardPaymentCharge(true));

        //when
        underTest.chargeCard(customerID, paymentRequest);

        //then
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentCaptorArgumentCaptorValue = paymentArgumentCaptor.getValue();
        assertThat(paymentCaptorArgumentCaptorValue).isEqualToIgnoringGivenFields(paymentRequest.getPayment(), "customerId");
        assertThat(paymentCaptorArgumentCaptorValue.getCustomerId()).isEqualTo(customerID);
    }

    @Test
    void itShouldThrowExceptionWhenTheCustomerIsNotFound() {
        //given
        UUID customerID = UUID.randomUUID();
        Currency currency = Currency.EUR;
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(null, null, new BigDecimal(300), currency, "card456", "donation"));
        given(customerRepository.findById(customerID)).willReturn(Optional.empty());
        //when then
        assertThatThrownBy(() -> underTest.chargeCard(customerID, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("customer does not exist");

    }

    @Test
    void itShouldThrowExceptionIfTheCurrencyIsNull() {
        //given
        UUID customerId = UUID.randomUUID();
        Currency currency = null;
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(null, null, new BigDecimal(100), currency, "card34567", "donation"));
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //when then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("currency %s is not supported", currency));
    }

    @Test
    void itShouldThrowExceptionIfTheCardIsNotDebited() {
        //given
        UUID customerID = UUID.randomUUID();
        PaymentRequest paymentRequest = new PaymentRequest(new Payment(null, null, new BigDecimal(100), Currency.EUR, "card678", "donation"));
        given(customerRepository.findById(customerID)).willReturn(Optional.of(mock(Customer.class)));
        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(false));

        //when then
       assertThatThrownBy(()->underTest.chargeCard(customerID, paymentRequest))
               .isInstanceOf(IllegalStateException.class)
               .hasMessageContaining(String.format("card was not debited for customer %s", customerID ));
    }
}