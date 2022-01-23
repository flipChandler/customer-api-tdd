package com.amigoscode.customerapitdd.service;

import com.amigoscode.customerapitdd.dto.PaymentRequest;
import com.amigoscode.customerapitdd.interfaces.CardCharger;
import com.amigoscode.customerapitdd.model.Currency;
import com.amigoscode.customerapitdd.repository.CustomerRepository;
import com.amigoscode.customerapitdd.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.GBP);

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardCharger cardCharger;

    @Autowired
    public PaymentService(CustomerRepository customerRepository,
                          PaymentRepository paymentRepository,
                          CardCharger cardCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardCharger = cardCharger;
    }

    void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
        // 1. Does customer exists if not, throw
        boolean isCustomerFound  = customerRepository.findById(customerId).isPresent();
        if (!isCustomerFound) {
            throw new IllegalStateException(String.format("customer with id [%s] not found", customerId));
        }

        // 2. do we support the currency if not throw
        boolean isCurrencySupported = ACCEPTED_CURRENCIES.contains(paymentRequest.getPayment().getCurrency());

        if (!isCurrencySupported) {
            String message = String.format("currency [%s] not supported", paymentRequest.getPayment().getCurrency());
            throw new IllegalStateException(message);
        }

        // 3. charge card
        CardPaymentCharge cardPaymentCharge = cardCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        // 4. if not debited, throw
        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException(String.format("card not debited for customer [%s]", customerId));
        }

        // 5. insert payment
        paymentRequest.getPayment().setCustomerId(customerId);
        paymentRepository.save(paymentRequest.getPayment());

        // TODO: SEND SMS
    }
}
