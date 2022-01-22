package com.amigoscode.customerapitdd.repository;

import com.amigoscode.customerapitdd.model.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

@DataJpaTest(
        properties = {"spring.jpa.properties.javax.persistence.validation.mode=none"}
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "0000";
        Customer customer = new Customer(id, "Abel", phoneNumber);

        // When
        underTest.save(customer);

        // Then
        Optional<Customer> optional = underTest.selectCustomerByPhoneNumber(phoneNumber);
        Assertions.assertThat(optional)
                .isPresent()
                .hasValueSatisfying(c -> {
                    Assertions.assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itNotShouldSelectCustomerByPhoneNumberWhenNumberDoesNotExists() {
        // Given
        String phoneNumber = "0000";

        // When
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);

        // Then
        Assertions.assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void itShouldSaveCustomer() {
        // given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Abel", "0000");

        // when
        underTest.save(customer);

        // then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        Assertions.assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    Assertions.assertThat(c.getId()).isEqualTo(id);
                    Assertions.assertThat(c.getName()).isEqualTo("Abel");
                    Assertions.assertThat(c.getPhoneNumber()).isEqualTo("0000");
                });
    }

    @Test
    void itShouldSaveCustomer2() {
        // given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Abel", "0000");

        // when
        underTest.save(customer);

        // then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        Assertions.assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(expected -> {
                    Assertions.assertThat(expected).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "0000");

        // When
        // Then
        Assertions.assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.customerapitdd.model.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Alex", null);

        // When
        // Then
        Assertions.assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.customerapitdd.model.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}