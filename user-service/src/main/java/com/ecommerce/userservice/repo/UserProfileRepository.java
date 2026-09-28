package com.ecommerce.userservice.repo;

import com.ecommerce.userservice.domain.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
}
