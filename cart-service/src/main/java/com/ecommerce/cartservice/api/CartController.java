package com.ecommerce.cartservice.api;

import com.ecommerce.cartservice.domain.Cart;
import com.ecommerce.cartservice.repo.CartRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartRepository repo;
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    public CartController(CartRepository r, StringRedisTemplate redis, ObjectMapper mapper) {
        repo = r;
        this.redis = redis;
        this.mapper = mapper;
    }

    @GetMapping
    public Cart get(Authentication a) {
        try {
            var cached = redis.opsForValue().get( "cart:" + a.getName() );
            if (cached != null) return mapper.readValue( cached, Cart.class );
        } catch (Exception ignored) {
        }
        var c = repo.findById( a.getName() ).orElseGet( () -> {
            var x = new Cart();
            x.userId = a.getName();
            return repo.save( x );
        } );
        try {
            redis.opsForValue().set( "cart:" + a.getName(), mapper.writeValueAsString( c ), java.time.Duration.ofMinutes( 10 ) );
        } catch (Exception ignored) {
        }
        return c;
    }

    @PostMapping("/items")
    public Cart add(Authentication a, @Valid @RequestBody AddItem r) {
        var c = get( a );
        var found = c.items.stream().filter( i -> i.productId().equals( r.productId() ) ).findFirst();
        if (found.isPresent()) {
            var old = found.get();
            c.items.remove( old );
            c.items.add( new Cart.Item( old.productId(), old.sku(), old.name(), old.unitPrice(), old.quantity() + r.quantity() ) );
        } else c.items.add( new Cart.Item( r.productId(), r.sku(), r.name(), r.unitPrice(), r.quantity() ) );
        c.updatedAt = java.time.Instant.now();
        var saved = repo.save( c );
        try {
            redis.opsForValue().set( "cart:" + a.getName(), mapper.writeValueAsString( saved ), java.time.Duration.ofMinutes( 10 ) );
        } catch (Exception ignored) {
        }
        return saved;
    }

    @DeleteMapping("/items/{productId}")
    public Cart remove(Authentication a, @PathVariable String productId) {
        var c = get( a );
        c.items.removeIf( i -> i.productId().equals( productId ) );
        var saved = repo.save( c );
        try {
            redis.opsForValue().set( "cart:" + a.getName(), mapper.writeValueAsString( saved ), java.time.Duration.ofMinutes( 10 ) );
        } catch (Exception ignored) {
        }
        return saved;
    }

    @DeleteMapping
    public void clear(Authentication a) {
        repo.deleteById( a.getName() );
        redis.delete( "cart:" + a.getName() );
    }

    public record AddItem(@NotBlank String productId, @NotBlank String sku, @NotBlank String name,
                          @Positive BigDecimal unitPrice, @Positive int quantity) {
    }
}
