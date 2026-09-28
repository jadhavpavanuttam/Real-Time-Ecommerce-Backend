package com.ecommerce.orderservice.repo;

import com.ecommerce.orderservice.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByUserIdAndIdempotencyKey(String userId, String key);
}
