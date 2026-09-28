package com.ecommerce.cartservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("carts")
public class Cart {
    @Id
    public String userId;
    public List<Item> items = new ArrayList<>();
    public Instant updatedAt = Instant.now();

    public record Item(String productId, String sku, String name, BigDecimal unitPrice, int quantity) {
    }
}
