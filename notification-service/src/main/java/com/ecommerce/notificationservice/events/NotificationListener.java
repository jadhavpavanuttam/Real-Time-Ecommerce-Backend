package com.ecommerce.notificationservice.events;

import com.ecommerce.notificationservice.domain.Notification;
import com.ecommerce.notificationservice.repo.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NotificationListener {
    private final NotificationRepository repo;
    private final ProcessedEventRepository processed;
    private final ObjectMapper mapper;
    private final SimpMessagingTemplate ws;

    public NotificationListener(NotificationRepository r, ProcessedEventRepository p, ObjectMapper m, SimpMessagingTemplate w) {
        repo = r;
        processed = p;
        mapper = m;
        ws = w;
    }

    @KafkaListener(topics = {"order.events", "inventory.events", "payment.events"}, groupId = "notification-service")
    public void onEvent(String raw) throws Exception {
        var e = mapper.readValue( raw, DomainEvent.class );
        if (processed.existsById( e.eventId() )) return;
        var n = new Notification();
        n.id = UUID.randomUUID().toString();
        n.userId = String.valueOf( e.payload().getOrDefault( "userId", "unknown" ) );
        n.orderId = e.aggregateId();
        n.type = e.type();
        n.message = "Order " + e.aggregateId() + " status: " + e.type();
        repo.save( n );
        if (!"unknown".equals( n.userId )) ws.convertAndSend( "/topic/orders/" + n.userId, n );
        processed.save( new ProcessedEvent( e.eventId() ) );
    }
}
