package com.roamandframe.checkout.client;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentClient {

    /** Refused (402) if declined, (409) if already voided. A repeat returns the same payment. */
    PaymentResult authorize(UUID orderId, UUID customerId, BigDecimal amount);

    /** Undo of authorize; safe to repeat. */
    void voidAuthorization(UUID orderId);
}
