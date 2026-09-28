package com.ecommerce.authservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("credentials")
public class UserCredential {
    @Id
    public String id;
    @Indexed(unique = true)
    public String email;
    public String passwordHash;
    public String role = "CUSTOMER";
    public Instant createdAt = Instant.now();
}
