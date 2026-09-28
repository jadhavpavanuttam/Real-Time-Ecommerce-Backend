package com.ecommerce.paymentservice.repo;

import com.ecommerce.paymentservice.domain.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByIdempotencyKey(String key);
}