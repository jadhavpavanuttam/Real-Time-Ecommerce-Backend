package com.ecommerce.cartservice.repo;

import com.ecommerce.cartservice.domain.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {
}
