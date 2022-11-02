package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;
    private List<Currency> acceptedCurrencies = List.of(Currency.EUR, Currency.USD);

    public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository, CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    void chargeCard(UUID customerID, PaymentRequest paymentRequest) {
        Optional<Customer> customerOptional = customerRepository.findById(customerID);
        if(customerOptional.isEmpty()) {
            throw new IllegalStateException("customer does not exist");
        }
        boolean isCurrencyAccepted = acceptedCurrencies.stream().anyMatch(c -> c.equals(paymentRequest.getPayment().getCurrency()));
        if(!isCurrencyAccepted) {
            String message = String.format("currency %s is not supported", paymentRequest.getPayment().getCurrency());
            throw new IllegalStateException(message);
        }
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );
        if(!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException(String.format("card was not debited for customer %s", customerID ));
        }
        paymentRequest.getPayment().setCustomerId(customerID);
        paymentRepository.save(paymentRequest.getPayment());

    }
}
