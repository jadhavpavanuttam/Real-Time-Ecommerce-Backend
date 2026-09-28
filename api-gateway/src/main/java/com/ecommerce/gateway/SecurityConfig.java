package com.ecommerce.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityWebFilterChain security(ServerHttpSecurity http) {
        return http
                .csrf( ServerHttpSecurity.CsrfSpec::disable )
                .authorizeExchange( a ->
                        a.pathMatchers( "/api/auth/**", "/actuator/health/**" )
                                .permitAll()
                                .anyExchange()
                                .authenticated() )
                .oauth2ResourceServer( o ->
                        o.jwt( j -> {
                        } ) ).build();
    }
}


//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityWebFilterChain security(
//            ServerHttpSecurity http) {
//
//        return http
//                .csrf( ServerHttpSecurity.CsrfSpec::disable )
//
//                .authorizeExchange( exchange -> exchange
//
//                        .pathMatchers(
//                                "/auth/**",
//                                "/public/**"
//                        ).permitAll()
//
//                        .anyExchange()
//                        .authenticated()
//                )
//
//                .oauth2ResourceServer( oauth2 ->
//                        oauth2.jwt( jwt -> {
//                        } )
//                )
//
//                .build();
//    }
//}