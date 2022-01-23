package com.amigoscode.customerapitdd.repository;

import com.amigoscode.customerapitdd.model.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
