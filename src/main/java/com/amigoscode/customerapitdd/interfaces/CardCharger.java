package com.amigoscode.customerapitdd.interfaces;

import com.amigoscode.customerapitdd.service.CardPaymentCharge;
import com.amigoscode.customerapitdd.model.Currency;

import java.math.BigDecimal;

public interface CardCharger {

    CardPaymentCharge chargeCard(
            String cardSource,
            BigDecimal amount,
            Currency currency,
            String description
    );
}
