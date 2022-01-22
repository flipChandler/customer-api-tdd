package com.amigoscode.customerapitdd.service;

import com.amigoscode.customerapitdd.dto.CustomerRequest;
import com.amigoscode.customerapitdd.model.Customer;
import com.amigoscode.customerapitdd.repository.CustomerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // given phone number and a customer
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Joseph", phoneNumber);

        // ... a request
        CustomerRequest request = new CustomerRequest(customer);

        // ... no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // when
        underTest.registerNewCustomer(request);

        // then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer argumentCaptorValue = customerArgumentCaptor.getValue();
        Assertions.assertThat(argumentCaptorValue).isEqualTo(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        // given phone number and a customer
        String phoneNumber = "000099";
        Customer customer = new Customer(null, "Joseph", phoneNumber);

        // ... a request
        CustomerRequest request = new CustomerRequest(customer);

        // ... no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // when
        underTest.registerNewCustomer(request);

        // then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer argumentCaptorValue = customerArgumentCaptor.getValue();
        Assertions.assertThat(argumentCaptorValue).isEqualToIgnoringGivenFields(customer, "id");    // UUID.random will set a new id, so this field must be ignored
        Assertions.assertThat(argumentCaptorValue.getId()).isNotNull();                                                   // if id is captured, customer was saved
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        // given phone number and a customer
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Joseph", phoneNumber);

        // ... a request
        CustomerRequest request = new CustomerRequest(customer);

        // ... an existing customer is returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

        // when
        underTest.registerNewCustomer(request);

        // then
        then(customerRepository).should(never()).save(any());
        // then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
        // then(customerRepository).shouldHaveNoMoreInteractions();
        // then(customerRepository).shouldHaveNoInteractions();  // when repository has no interaction int the method
    }

    @Test
    void itShouldThrowAnExceptionWhenPhoneIsTaken(){
        // given phone number and a customer
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Joseph", phoneNumber);
        Customer differentCustomer = new Customer(UUID.randomUUID(), "Shirley", phoneNumber);
        // ... a request
        CustomerRequest request = new CustomerRequest(customer);

        // ... no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(differentCustomer));

        // when
        // then
        Assertions.assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber));

        then(customerRepository).should(never()).save(any(Customer.class));
    }
}