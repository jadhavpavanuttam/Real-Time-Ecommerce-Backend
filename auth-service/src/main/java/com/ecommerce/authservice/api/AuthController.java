package com.ecommerce.authservice.api;

import com.ecommerce.authservice.domain.UserCredential;
import com.ecommerce.authservice.repo.UserCredentialRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserCredentialRepository repo;
    private final PasswordEncoder encoder;
    private final SecretKey key;

    public AuthController(UserCredentialRepository repo, PasswordEncoder encoder, @Value("${app.jwt.secret}") String secret) {
        this.repo = repo;
        this.encoder = encoder;
        this.key = Keys.hmacShaKeyFor( secret.getBytes( StandardCharsets.UTF_8 ) );
    }

    @GetMapping("/helth-check")
    public String healthCheck() {
        return "Okay";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r) {
        if (repo.findByEmail( r.email() ).isPresent()) throw new IllegalArgumentException( "Email already registered" );
        var u = new UserCredential();
        u.email = r.email().toLowerCase();
        u.passwordHash = encoder.encode( r.password() );
        repo.save( u );
        return ResponseEntity.status( 201 ).body( Map.of( "id", u.id, "email", u.email, "role", u.role ) );
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest r) {
        var u = repo.findByEmail( r.email().toLowerCase() ).orElseThrow( () -> new IllegalArgumentException( "Invalid credentials" ) );
        if (!encoder.matches( r.password(), u.passwordHash ))
            throw new IllegalArgumentException( "Invalid credentials" );
        var now = Instant.now();
        var exp = now.plusSeconds( 3600 );
        String token = Jwts.builder().subject( u.id ).claim( "email", u.email ).claim( "role", u.role ).issuedAt( Date.from( now ) ).expiration( Date.from( exp ) ).signWith( key ).compact();
        return new TokenResponse( token, "Bearer", 3600, u.id, u.role );
    }

    public record RegisterRequest(@Email @NotBlank String email, @NotBlank String password) {
    }

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
    }

    public record TokenResponse(String accessToken, String tokenType, long expiresIn, String userId, String role) {
    }
}
