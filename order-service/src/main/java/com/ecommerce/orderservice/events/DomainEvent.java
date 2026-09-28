package com.ecommerce.orderservice.events;

import java.time.Instant;
import java.util.Map;

public record DomainEvent(String eventId, String type, Instant occurredAt, String aggregateId,
                          Map<String, Object> payload) {
    public static DomainEvent of(String type, String aggregateId, Map<String, Object> payload) {
        return new DomainEvent( java.util.UUID.randomUUID().toString(), type, Instant.now(), aggregateId, payload );
    }
}
