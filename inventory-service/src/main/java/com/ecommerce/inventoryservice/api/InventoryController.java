package com.ecommerce.inventoryservice.api;

import com.ecommerce.inventoryservice.domain.Inventory;
import com.ecommerce.inventoryservice.repo.InventoryRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryRepository repo;

    public InventoryController(InventoryRepository r) {
        repo = r;
    }

    @GetMapping("/{productId}")
    public Inventory get(@PathVariable String productId) {
        return repo.findByProductId( productId ).orElseThrow( () -> new IllegalArgumentException( "Inventory not found" ) );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Inventory create(@RequestBody Inventory i) {
        return repo.save( i );
    }

    @GetMapping
    public List<Inventory> all() {
        return repo.findAll();
    }
}
