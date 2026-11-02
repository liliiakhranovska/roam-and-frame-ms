package com.roamandframe.payment.exception;

import java.util.UUID;

public class PaymentVoidedException extends RuntimeException {
    public PaymentVoidedException(UUID orderId) {
        super("Payment for order " + orderId + " was already voided");
    }
}
