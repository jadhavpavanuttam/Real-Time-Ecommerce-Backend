package com.ecommerce.orderservice.api;

import com.ecommerce.orderservice.domain.Order;
import com.ecommerce.orderservice.events.DomainEvent;
import com.ecommerce.orderservice.events.KafkaPublisher;
import com.ecommerce.orderservice.repo.OrderRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository repo;
    private final KafkaPublisher publisher;

    public OrderController(OrderRepository r, KafkaPublisher p) {
        repo = r;
        publisher = p;
    }

    @PostMapping
    public Order create(Authentication a, @RequestHeader("Idempotency-Key") @NotBlank String key, @Valid @RequestBody CreateOrderRequest r) {
        var existing = repo.findByUserIdAndIdempotencyKey( a.getName(), key );
        if (existing.isPresent()) return existing.get();
        var o = new Order();
        o.id = UUID.randomUUID().toString();
        o.userId = a.getName();
        o.idempotencyKey = key;
        o.items = r.items().stream().map( i -> new Order.Item( i.productId(), i.sku(), i.name(), i.unitPrice(), i.quantity() ) ).toList();
        o.total = o.items.stream().map( i -> i.unitPrice().multiply( BigDecimal.valueOf( i.quantity() ) ) ).reduce( BigDecimal.ZERO, BigDecimal::add );
        repo.save( o );
        publisher.publish( "order.events", DomainEvent.of( "ORDER_CREATED", o.id, Map.of( "orderId", o.id, "userId", o.userId, "items", o.items, "total", o.total ) ) );
        return o;
    }

    @GetMapping
    public List<Order> mine(Authentication a) {
        return repo.findAll().stream().filter( o -> o.userId.equals( a.getName() ) ).toList();
    }

    @GetMapping("/{id}")
    public Order get(Authentication a, @PathVariable String id) {
        var o = repo.findById( id ).orElseThrow( () -> new IllegalArgumentException( "Order not found" ) );
        if (!o.userId.equals( a.getName() )) throw new IllegalArgumentException( "Order not found" );
        return o;
    }

    public record CreateOrderRequest(@Size(min = 1) List<Item> items) {
    }

    public record Item(@NotBlank String productId, @NotBlank String sku, @NotBlank String name,
                       @Positive BigDecimal unitPrice, @Positive int quantity) {
    }
}
