package com.ecommerce.cartservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ProductClientConfig {
    @Bean
    RestClient productRestClient() {
        return RestClient
                .builder()
                .baseUrl( System.getenv()
                        .getOrDefault( "PRODUCT_SERVICE_URL", "http://product-service:8083" ) )
                .build();
    }
}
