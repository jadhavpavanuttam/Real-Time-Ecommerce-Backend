package com.ecommerce.paymentservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document("payments")
public class Payment {
    @Id
    public String id;
    @Indexed(unique = true)
    public String idempotencyKey;
    public String orderId;
    public String userId;
    public BigDecimal amount;
    public String status;
    public String providerTransactionId;
    public Instant createdAt = Instant.now();
    public Instant updatedAt = Instant.now();
}
