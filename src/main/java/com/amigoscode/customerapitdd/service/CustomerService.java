package com.amigoscode.customerapitdd.service;

import com.amigoscode.customerapitdd.dto.CustomerRequest;
import com.amigoscode.customerapitdd.model.Customer;
import com.amigoscode.customerapitdd.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRequest request) {
        // 1. PhoneNumber is taken
        // 2. if taken lets check if belongs to the same customer
        // - 2.1 if yes return
        // - 2.2 thrown an exception
        // 3. save customer
        String phoneNumber = request.getCustomer().getPhoneNumber();

        Optional<Customer> optional = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        if (optional.isPresent()) {
            Customer customer = optional.get();
            if (customer.getName().equals(request.getCustomer().getName())) {
                return;
            }
            throw new IllegalStateException(String.format("phone number [%s] is taken", phoneNumber));
        }

        if (request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }
        customerRepository.save(request.getCustomer());
    }
}
