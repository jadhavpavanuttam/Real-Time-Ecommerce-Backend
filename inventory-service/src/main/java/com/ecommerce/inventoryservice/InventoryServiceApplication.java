package com.ecommerce.inventoryservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run( InventoryServiceApplication.class, args );
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Vital for E-commerce events: Handles Java 8 time formats like Instant/LocalDateTime
        mapper.registerModule( new JavaTimeModule() );
        return mapper;
    }
}
