package com.ecommerce.gateway.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
@Configuration
public class JwtConfig {

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {

        String secret = "your-very-long-secret-key";

        SecretKey key = new SecretKeySpec(
                secret.getBytes(),
                "HmacSHA256"
        );

        return NimbusReactiveJwtDecoder
                .withSecretKey( key )
                .build();
    }
}