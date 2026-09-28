package com.ecommerce.notificationservice.events;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("processed_events")
public class ProcessedEvent {
    @Id
    public String id;
    public Instant processedAt = Instant.now();

    public ProcessedEvent() {
    }

    public ProcessedEvent(String id) {
        this.id = id;
    }
}
