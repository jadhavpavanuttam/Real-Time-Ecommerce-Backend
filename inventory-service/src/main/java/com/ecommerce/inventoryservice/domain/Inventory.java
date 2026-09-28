package com.ecommerce.inventoryservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("inventory")
public class Inventory {
    @Id
    public String id;
    @Indexed(unique = true)
    public String productId;
    public int availableStock;
    public int reservedStock;
    public Instant updatedAt = Instant.now();
}
