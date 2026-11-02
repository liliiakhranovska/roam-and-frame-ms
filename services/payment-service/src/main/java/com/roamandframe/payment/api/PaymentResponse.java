package com.roamandframe.payment.api;

import com.roamandframe.payment.model.Payment;
import com.roamandframe.payment.model.PaymentStatus;

import java.util.UUID;

record PaymentResponse(UUID orderId, PaymentStatus status, String reference) {
    static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.orderId(), payment.status(), payment.reference());
    }
}
