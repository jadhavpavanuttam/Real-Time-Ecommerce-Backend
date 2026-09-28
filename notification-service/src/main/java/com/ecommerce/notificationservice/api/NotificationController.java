package com.ecommerce.notificationservice.api;

import com.ecommerce.notificationservice.repo.NotificationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationRepository repo;

    public NotificationController(NotificationRepository r) {
        repo = r;
    }

    @GetMapping
    public Object mine(Authentication a) {
        return repo.findByUserIdOrderByCreatedAtDesc( a.getName() );
    }
}
