package com.amigoscode.customerapitdd.dto;

import com.amigoscode.customerapitdd.model.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerRequest {

    private final Customer customer;

    public CustomerRequest(@JsonProperty Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String toString() {
        return "CustomerRequest{" + "customer=" + customer + '}';
    }
}
