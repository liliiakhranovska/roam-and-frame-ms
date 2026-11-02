package com.roamandframe.payment.repository;


import com.roamandframe.payment.model.Payment;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Optional<Payment> findByOrderId(UUID orderId);
    Payment create(UUID orderId, UUID customerId, BigDecimal amount, String reference);
    void markVoided(UUID orderId);
}
