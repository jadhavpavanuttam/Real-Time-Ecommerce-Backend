package com.ecommerce.orderservice.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPublisher {
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;

    public KafkaPublisher(KafkaTemplate<String, String> k, ObjectMapper m) {
        kafka = k;
        mapper = m;
    }

    public void publish(String topic, DomainEvent e) {
        try {
            kafka.send( topic, e.aggregateId(), mapper.writeValueAsString( e ) );
        } catch (Exception ex) {
            throw new IllegalStateException( "Failed to publish event", ex );
        }
    }
}