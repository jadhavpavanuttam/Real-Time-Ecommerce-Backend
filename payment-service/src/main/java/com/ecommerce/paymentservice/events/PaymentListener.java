package com.ecommerce.paymentservice.events;

import com.ecommerce.paymentservice.domain.Payment;
import com.ecommerce.paymentservice.repo.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class PaymentListener {
    private final PaymentRepository repo;
    private final ProcessedEventRepository processed;
    private final KafkaPublisher pub;
    private final ObjectMapper mapper;

    public PaymentListener(PaymentRepository r, ProcessedEventRepository p, KafkaPublisher pub, ObjectMapper m) {
        repo = r;
        processed = p;
        this.pub = pub;
        mapper = m;
    }

    @KafkaListener(topics = "inventory.events", groupId = "payment-service")
    public void reserved(String raw) throws Exception {
        var e = mapper.readValue( raw, DomainEvent.class );
        if (!"INVENTORY_RESERVED".equals( e.type() ) || processed.existsById( e.eventId() )) return;
        String orderId = e.aggregateId();
        String idem = "order:" + orderId;
        var pay = repo.findByIdempotencyKey( idem ).orElseGet( () -> {
            var x = new Payment();
            x.id = UUID.randomUUID().toString();
            x.idempotencyKey = idem;
            x.orderId = orderId;
            x.userId = String.valueOf( e.payload().getOrDefault( "userId", "unknown" ) );
            x.amount = new BigDecimal( String.valueOf( e.payload().getOrDefault( "total", "0" ) ) );
            x.status = "SUCCESS";
            x.providerTransactionId = "MOCK-" + UUID.randomUUID();
            return repo.save( x );
        } );
        processed.save( new ProcessedEvent( e.eventId() ) );
        pub.publish( "payment.events", DomainEvent.of( "PAYMENT_SUCCESS", orderId, Map.of( "paymentId", pay.id, "orderId", orderId ) ) );
    }
}
