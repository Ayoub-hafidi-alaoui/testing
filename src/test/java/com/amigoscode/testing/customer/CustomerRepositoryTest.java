package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        //given

        //when
        //then
    }

    @Test
    void itShouldSaveCustomer() {
        //given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "ayoub", "678");
        //when
        underTest.save(customer);
        //then
        Optional<Customer> customerOptional = underTest.findById(id);
        assertThat(customerOptional)
            .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        //given
        String phoneNumber = "4567";
        Customer customer = new Customer(UUID.randomUUID(), null, phoneNumber);

        //when
        underTest.save(customer);

        //then
        assertThatThrownBy(()->underTest.findCustomerByPhoneNumber(phoneNumber))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Validation failed for classes [com.amigoscode.testing.customer.Customer] during persist time for groups [javax.validation.groups.Default, ]");
    }

    @Test
    void itShouldFindCustomerByPhoneNumber() {
        //given
        String phoneNumber = "4567";
        Customer customer = new Customer(UUID.randomUUID(), "ayoub", phoneNumber);

        //when
        underTest.save(customer);
        Optional<Customer> optionalCustomer = underTest.findCustomerByPhoneNumber(customer.getPhoneNumber());

        //then
        assertThat(optionalCustomer).isPresent().hasValueSatisfying(c ->{
            assertThat(c).isEqualToComparingFieldByField(customer);
        });
    }

    @Test
    void itShouldNotSelectCustomerWhenThePhoneNumberDoesNotExist() {
        //given
        String phoneNumber = "45678";

        //when
        Optional<Customer> customerOptional = underTest.findCustomerByPhoneNumber(phoneNumber);

        //then
        assertThat(customerOptional).isEmpty();
    }
}