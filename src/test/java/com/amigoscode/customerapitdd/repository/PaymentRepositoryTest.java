package com.amigoscode.customerapitdd.repository;

import com.amigoscode.customerapitdd.model.Currency;
import com.amigoscode.customerapitdd.model.Payment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest(properties = {"spring.jpa.properties.javax.persistence.validation.mode=none"})
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;

    @Test
    void itShouldInsertPayment() {
        // given
        long paymentId = 1L;
        Payment payment = new Payment(paymentId,
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                Currency.USD,
                "card123",
                "Donation");

        // when
        underTest.save(payment);

        // then
        Optional<Payment> optional = underTest.findById(paymentId);
        Assertions.assertThat(optional)
                .isPresent()
                .hasValueSatisfying(p -> {
                    //Assertions.assertThat(p).isEqualTo(payment);
                    Assertions.assertThat(p).isEqualToComparingFieldByField(payment);
                });
    }
}