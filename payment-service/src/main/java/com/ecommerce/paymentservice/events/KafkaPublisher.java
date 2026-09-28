package com.ecommerce.paymentservice.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPublisher {
    private final KafkaTemplate<String, String> k;
    private final ObjectMapper m;

    public KafkaPublisher(KafkaTemplate<String, String> k, ObjectMapper m) {
        this.k = k;
        this.m = m;
    }

    public void publish(String t, DomainEvent e) {
        try {
            k.send( t, e.aggregateId(), m.writeValueAsString( e ) );
        } catch (Exception x) {
            throw new IllegalStateException( x );
        }
    }
}