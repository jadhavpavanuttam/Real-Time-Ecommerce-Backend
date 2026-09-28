package com.ecommerce.authservice.repo;

import com.ecommerce.authservice.domain.UserCredential;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserCredentialRepository extends MongoRepository<UserCredential, String> {
    Optional<UserCredential> findByEmail(String email);
}
