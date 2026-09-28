package com.ecommerce.inventoryservice.events;

import com.ecommerce.inventoryservice.domain.Inventory;
import com.ecommerce.inventoryservice.repo.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InventorySagaListener {
    private final InventoryRepository repo;
    private final ProcessedEventRepository processed;
    private final KafkaPublisher pub;
    private final ObjectMapper mapper;
    private final MongoTemplate mongo;

    public InventorySagaListener(InventoryRepository r, ProcessedEventRepository p, KafkaPublisher pub, ObjectMapper m, MongoTemplate mongo) {
        repo = r;
        processed = p;
        this.pub = pub;
        mapper = m;
        this.mongo = mongo;
    }

    @KafkaListener(topics = "order.events", groupId = "inventory-service")
    public void orderCreated(String raw) throws Exception {
        var e = mapper.readValue( raw, DomainEvent.class );
        if (!"ORDER_CREATED".equals( e.type() ) || processed.existsById( e.eventId() )) return;
        boolean ok = true;
        var reserved = new ArrayList<Map<String, Object>>();
        var items = (List<?>) e.payload().get( "items" );
        for (Object obj : items) {
            var map = mapper.convertValue( obj, Map.class );
            String productId = (String) map.get( "productId" );
            int qty = ((Number) map.get( "quantity" )).intValue();
            Query q = new Query( Criteria.where( "productId" ).is( productId ).and( "availableStock" ).gte( qty ) );
            Update u = new Update().inc( "availableStock", -qty ).inc( "reservedStock", qty ).set( "updatedAt", java.time.Instant.now() );
            var result = mongo.updateFirst( q, u, Inventory.class );
            if (result.getModifiedCount() == 0) {
                ok = false;
                break;
            }
            reserved.add( map );
        }
        if (!ok) {
            for (var map : reserved) {
                String productId = (String) map.get( "productId" );
                int qty = ((Number) map.get( "quantity" )).intValue();
                mongo.updateFirst( new Query( Criteria.where( "productId" ).is( productId ) ), new Update().inc( "availableStock", qty ).inc( "reservedStock", -qty ), Inventory.class );
            }
        }
        processed.save( new ProcessedEvent( e.eventId() ) );
        var payload = new HashMap<String, Object>( e.payload() );
        payload.put( "orderId", e.aggregateId() );
        pub.publish( "inventory.events", DomainEvent.of( ok ? "INVENTORY_RESERVED" : "INVENTORY_FAILED", e.aggregateId(), payload ) );
    }

    @KafkaListener(topics = "payment.events", groupId = "inventory-compensation")
    public void paymentFailed(String raw) throws Exception {
        var e = mapper.readValue( raw, DomainEvent.class );
        if (!"PAYMENT_FAILED".equals( e.type() ) || processed.existsById( e.eventId() )) return;
        var order = (Map<?, ?>) e.payload();
        Object itemsObj = order.get( "items" );
        List<?> items = itemsObj instanceof List<?> l ? l : List.of();
        for (Object obj : items) {
            var m = mapper.convertValue( obj, Map.class );
            String productId = (String) m.get( "productId" );
            int qty = ((Number) m.get( "quantity" )).intValue();
            Query q = new Query( Criteria.where( "productId" ).is( productId ) );
            Update u = new Update().inc( "availableStock", qty ).inc( "reservedStock", -qty );
            mongo.updateFirst( q, u, Inventory.class );
        }
        processed.save( new ProcessedEvent( e.eventId() ) );
    }
}
