package com.ecommerce.productservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document("products")
@CompoundIndex(name = "category_active_idx", def = "{'category':1,'active':1}")
@CompoundIndex(name = "name_text_idx", def = "{'name':'text','description':'text'}")
public class Product {
    @Id
    public String id;
    public String sku;
    public String name;
    public String description;
    public String category;
    public BigDecimal price;
    public String currency = "INR";
    public boolean active = true;
    public Instant createdAt = Instant.now();
    public Instant updatedAt = Instant.now();
}
