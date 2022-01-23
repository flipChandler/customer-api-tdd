package com.amigoscode.customerapitdd.service;

import com.amigoscode.customerapitdd.dto.PaymentRequest;
import com.amigoscode.customerapitdd.interfaces.CardCharger;
import com.amigoscode.customerapitdd.model.Currency;
import com.amigoscode.customerapitdd.model.Customer;
import com.amigoscode.customerapitdd.model.Payment;
import com.amigoscode.customerapitdd.repository.CustomerRepository;
import com.amigoscode.customerapitdd.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    private PaymentService underTest;

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardCharger cardCharger;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        // given
        UUID customerId = UUID.randomUUID();

        // ... customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(Mockito.mock(Customer.class)));

        // ... payment request
        Currency currency = Currency.USD;
        PaymentRequest request = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        currency,
                        "card123xx",
                        "Donation"
                )
        );

        // ... card is charged successfully
        given(cardCharger.chargeCard(
                request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));

        // when
        underTest.chargeCard(customerId, request);

        // then
        ArgumentCaptor<Payment> argumentCaptor = ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(argumentCaptor.capture());

        Payment capturedValue = argumentCaptor.getValue();
        assertThat(capturedValue).isEqualToIgnoringGivenFields(
                request.getPayment(),"customerId");

        assertThat(capturedValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldThrownWhenCardIsNotCharged() {
        // given
        UUID customerId = UUID.randomUUID();

        // ... customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(Mockito.mock(Customer.class)));

        // ... payment request
        Currency currency = Currency.USD;
        PaymentRequest request = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        currency,
                        "card123xx",
                        "Donation"
                )
        );

        // ... card is not charged
        given(cardCharger.chargeCard(
                request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        // when
        // then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("card not debited for customer [%s]", customerId));

        then(paymentRepository).should(never()).save(any(Payment.class));
    }

    @Test
    void itShouldNotChargeCardAndThrownWhenCurrencyNotSupported() {
        // given
        UUID customerId = UUID.randomUUID();

        // ... customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(Mockito.mock(Customer.class)));

        // ... payment request
        Currency currency = Currency.EUR;
        PaymentRequest request = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        currency,
                        "card123xx",
                        "Donation"
                )
        );

        // when
        assertThatThrownBy(() -> underTest.chargeCard(customerId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("currency [%s] not supported", currency));

        // then
        // ... card is not charged cause EUR currency is not supported
        then(cardCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeAndThrownWhenCustomerNotFound() {
        // given
        UUID customerId = UUID.randomUUID();

        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("customer with id [%s] not found", customerId));

        then(cardCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }
}