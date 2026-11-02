package com.roamandframe.notification.api;

import com.roamandframe.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/notifications")
class NotificationController {

    private final NotificationService notificationService;

    NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/order-confirmation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void sendOrderConfirmation(@Valid @RequestBody OrderConfirmationRequest request) {
        notificationService.sendOrderConfirmation(request);
    }
}
