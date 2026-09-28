package com.ecommerce.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder( 12 );
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf( c -> c.disable() ).authorizeHttpRequests( a -> a
                .requestMatchers( "/actuator/health/**", "/actuator/info", "/v3/api-docs/**", "/swagger-ui/**" ).permitAll()
                .anyRequest().authenticated() ).oauth2ResourceServer( o -> o.jwt( j -> {
        } ) );
        return http.build();
    }
}
