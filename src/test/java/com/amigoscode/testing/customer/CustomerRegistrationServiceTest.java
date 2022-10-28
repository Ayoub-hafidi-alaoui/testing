package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;
    private CustomerRegistrationService underTest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() throws Exception {
        //given phone number and a customer
        String phoneNumber = "345677";
        Customer customer = new Customer(UUID.randomUUID(), "ayoub", phoneNumber);

        // .....a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //when
        underTest.registerNewCustomer(request);

        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        assertThat(customerArgumentCaptor.getValue()).isEqualToComparingFieldByField(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        //given
        String phoneNumber = "45678";
        Customer customer  = new Customer("ayoub", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        //when
        underTest.registerNewCustomer(request);

        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        assertThat(customerArgumentCaptor.getValue()).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptor.getValue().getId()).isNotNull();

    }

    @Test
    void itShouldNotSaveWhenCustomerExist() throws Exception {
        //given phone number and a customer
        String phoneNumber = "345677";
        Customer customer = new Customer(UUID.randomUUID(), "ayoub", phoneNumber);

        // .....a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

        //when
        underTest.registerNewCustomer(request);

        //then
        then(customerRepository).should().findCustomerByPhoneNumber(phoneNumber);
        then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldThrowAnException() {
        //given
        String phoneNumber = "34567";
        Customer customer = new Customer(UUID.randomUUID(), "ayoub", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.findCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(new Customer(UUID.randomUUID(), "ayoub", phoneNumber)));

        //when
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("something is wrong");

        //then
        then(customerRepository).should(never()).save(any());
    }


}