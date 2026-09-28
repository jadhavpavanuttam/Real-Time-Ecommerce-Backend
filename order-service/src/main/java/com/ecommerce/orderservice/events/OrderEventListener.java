package com.ecommerce.orderservice.events;

import com.ecommerce.orderservice.repo.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {
    private final OrderRepository repo;
    private final ProcessedEventRepository processed;
    private final ObjectMapper mapper;

    public OrderEventListener(OrderRepository r, ProcessedEventRepository p, ObjectMapper m) {
        repo = r;
        processed = p;
        mapper = m;
    }

    @KafkaListener(topics = "inventory.events", groupId = "order-service")
    public void inventory(String raw) throws Exception {
        handle( raw, "INVENTORY_RESERVED", "INVENTORY_FAILED" );
    }

    @KafkaListener(topics = "payment.events", groupId = "order-service")
    public void payment(String raw) throws Exception {
        handle( raw, "PAYMENT_SUCCESS", "PAYMENT_FAILED" );
    }

    private void handle(String raw, String success, String failure) throws Exception {
        var e = mapper.readValue( raw, DomainEvent.class );
        if (processed.existsById( e.eventId() )) return;
        var id = e.aggregateId();
        var o = repo.findById( id ).orElse( null );
        if (o == null) return;
        switch (e.type()) {
            case "INVENTORY_RESERVED" -> o.status = "PAYMENT_PENDING";
            case "INVENTORY_FAILED" -> o.status = "FAILED_INVENTORY";
            case "PAYMENT_SUCCESS" -> o.status = "CONFIRMED";
            case "PAYMENT_FAILED" -> o.status = "PAYMENT_FAILED";
            default -> {
            }
        }
        o.updatedAt = java.time.Instant.now();
        repo.save( o );
        processed.save( new ProcessedEvent( e.eventId() ) );
    }
}
