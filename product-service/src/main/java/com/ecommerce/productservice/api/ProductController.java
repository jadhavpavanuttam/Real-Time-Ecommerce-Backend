package com.ecommerce.productservice.api;

import com.ecommerce.productservice.domain.Product;
import com.ecommerce.productservice.repo.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repo;

    public ProductController(ProductRepository r) {
        repo = r;
    }

    @GetMapping
    public Page<Product> list(@RequestParam(required = false) String category, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        size = Math.min( size, 100 );
        var p = PageRequest.of( page, size, Sort.by( "createdAt" ).descending() );
        return category == null ? repo.findByActiveTrue( p ) : repo.findByCategoryAndActiveTrue( category, p );
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable String id) {
        return repo.findById( id ).orElseThrow( () -> new IllegalArgumentException( "Product not found" ) );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Product create(@Valid @RequestBody ProductRequest r) {
        var p = new Product();
        p.sku = r.sku();
        p.name = r.name();
        p.description = r.description();
        p.category = r.category();
        p.price = r.price();
        return repo.save( p );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Product update(@PathVariable String id, @Valid @RequestBody ProductRequest r) {
        var p = get( id );
        p.name = r.name();
        p.description = r.description();
        p.category = r.category();
        p.price = r.price();
        p.updatedAt = java.time.Instant.now();
        return repo.save( p );
    }

    public record ProductRequest(@NotBlank String sku, @NotBlank String name, String description,
                                 @NotBlank String category, @Positive BigDecimal price) {
    }
}
