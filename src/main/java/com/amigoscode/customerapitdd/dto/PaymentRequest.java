package com.amigoscode.customerapitdd.dto;

import com.amigoscode.customerapitdd.model.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {

    private final Payment payment;

    public PaymentRequest(@JsonProperty("payment") Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "payment=" + payment +
                '}';
    }
}
