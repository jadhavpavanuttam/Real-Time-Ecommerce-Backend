package com.ecommerce.userservice.api;

import com.ecommerce.userservice.domain.UserProfile;
import com.ecommerce.userservice.repo.UserProfileRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserProfileRepository repo;

    public UserController(UserProfileRepository r) {
        repo = r;
    }

    @GetMapping("/me")
    public UserProfile me(Authentication a) {
        return repo.findById( a.getName() ).orElseGet( () -> {
            var u = new UserProfile();
            u.id = a.getName();
            u.email = String.valueOf( a.getDetails() );
            return repo.save( u );
        } );
    }

    @PutMapping("/me")
    public UserProfile update(Authentication a, @Valid @RequestBody UpdateRequest r) {
        var u = repo.findById( a.getName() ).orElseThrow( () -> new IllegalArgumentException( "Profile not found" ) );
        u.firstName = r.firstName();
        u.lastName = r.lastName();
        u.phone = r.phone();
        u.updatedAt = java.time.Instant.now();
        return repo.save( u );
    }

    public record UpdateRequest(@NotBlank String firstName, @NotBlank String lastName, String phone) {
    }
}
