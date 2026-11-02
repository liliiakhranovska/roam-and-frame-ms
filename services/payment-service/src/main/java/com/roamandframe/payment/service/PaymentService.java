package com.roamandframe.payment.service;

import com.roamandframe.payment.model.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    Payment authorize(UUID orderId, UUID customerId, BigDecimal amount);

    void voidAuthorization(UUID orderId);
}
