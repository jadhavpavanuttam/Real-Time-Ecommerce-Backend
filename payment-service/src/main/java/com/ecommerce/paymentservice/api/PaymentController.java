package com.ecommerce.paymentservice.api;

import com.ecommerce.paymentservice.domain.Payment;
import com.ecommerce.paymentservice.repo.PaymentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentRepository repo;

    public PaymentController(PaymentRepository r) {
        repo = r;
    }

    @GetMapping("/{id}")
    public Payment get(Authentication a, @PathVariable String id) {
        var p = repo.findById( id ).orElseThrow( () -> new IllegalArgumentException( "Payment not found" ) );
        if (!p.userId.equals( a.getName() )) throw new IllegalArgumentException( "Payment not found" );
        return p;
    }

    @GetMapping
    public List<Payment> mine(Authentication a) {
        return repo.findAll().stream().filter( p -> a.getName().equals( p.userId ) ).toList();
    }
}
