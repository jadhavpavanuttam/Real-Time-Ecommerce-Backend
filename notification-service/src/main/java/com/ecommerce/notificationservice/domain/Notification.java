package com.ecommerce.notificationservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("notifications")
public class Notification {
    @Id
    public String id;
    public String userId;
    public String orderId;
    public String type;
    public String message;
    public boolean read;
    public Instant createdAt = Instant.now();
}