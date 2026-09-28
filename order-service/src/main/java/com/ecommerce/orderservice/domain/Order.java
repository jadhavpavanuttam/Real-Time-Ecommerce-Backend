package com.ecommerce.orderservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("orders")
public class Order {
    @Id
    public String id;
    @Indexed
    public String userId;
    public String status = "PENDING_INVENTORY";
    public BigDecimal total;
    public List<Item> items = new ArrayList<>();
    public String idempotencyKey;
    public Instant createdAt = Instant.now();
    public Instant updatedAt = Instant.now();

    public record Item(String productId, String sku, String name, BigDecimal unitPrice, int quantity) {
    }
}
