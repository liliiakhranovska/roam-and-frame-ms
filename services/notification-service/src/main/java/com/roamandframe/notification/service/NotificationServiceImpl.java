package com.roamandframe.notification.service;

import com.roamandframe.notification.api.OrderConfirmationRequest;
import com.roamandframe.notification.gateway.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final EmailSender emailSender;
    private final String currency;

    NotificationServiceImpl(EmailSender emailSender, @Value("${app.notification.currency}") String currency) {
        this.emailSender = emailSender;
        this.currency = currency;
    }

    @Async
    @Override
    public void sendOrderConfirmation(OrderConfirmationRequest request) {
        try {
            emailSender.send(request.customer().email(),
                    NotificationMessages.orderConfirmationSubject(),
                    NotificationMessages.orderConfirmationBody(request, currency));
            log.info("Order confirmation sent: order={}", request.order().id());
        } catch (RuntimeException e) {
            log.error("Failed to send order confirmation: order={}", request.order().id(), e);
        }
    }
}
