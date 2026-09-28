package com.ecommerce.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    // 1. Inject the secret key from your properties file
    @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
    private String secretKey;

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                this.secretKey.getBytes(),
                "HmacSHA256"
        );
        return NimbusJwtDecoder.withSecretKey( secretKeySpec ).build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder( 12 );
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf( AbstractHttpConfigurer::disable ).authorizeHttpRequests( a -> a
                        .requestMatchers( "/actuator/health/**", "/actuator/info", "/v3/api-docs/**", "/swagger-ui/**" )
                        .permitAll()
                        .anyRequest()
                        .authenticated() )
                .oauth2ResourceServer( o ->
                        o.jwt( j -> {
                        } ) );
        return http.build();
    }
}
