package com.roamandframe.payment.gateway;

import java.math.BigDecimal;
import java.util.UUID;

/** Port to the external payment provider. */
public interface PaymentGateway {

    /** Authorizes the amount and returns the provider's reference; throws PaymentDeclinedException if declined. */
    String authorize(UUID orderId, BigDecimal amount);

    void voidAuthorization(String reference);
}
