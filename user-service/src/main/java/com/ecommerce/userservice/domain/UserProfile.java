package com.ecommerce.userservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("profiles")
public class UserProfile {
    @Id
    public String id;
    public String email;
    public String firstName;
    public String lastName;
    public String phone;
    public Address address;
    public Instant createdAt = Instant.now();
    public Instant updatedAt = Instant.now();

    public record Address(String line1, String city, String state, String postalCode, String country) {

    }
}
