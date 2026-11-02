package com.roamandframe.notification.service;

import com.roamandframe.notification.api.OrderConfirmationRequest;

public interface NotificationService {

    void sendOrderConfirmation(OrderConfirmationRequest request);
}
