package com.amigoscode.testing.customer;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {
    private final CustomerRepository customerRepository;

    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request)  {
        Optional<Customer> customerOptional = customerRepository.findCustomerByPhoneNumber(request.getCustomer().getPhoneNumber());
        if (customerOptional.isPresent()) {
            if (customerOptional.get().equals(request.getCustomer())) {
                return;
            } else {
                throw new IllegalStateException("something is wrong");
            }
        }
        if(request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }
        customerRepository.save(request.getCustomer());
    }
}
