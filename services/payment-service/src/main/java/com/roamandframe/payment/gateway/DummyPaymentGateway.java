package com.roamandframe.payment.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/** Stand-in until a real provider is integrated: always authorizes. */
@Component
class DummyPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(DummyPaymentGateway.class);

    @Override
    public String authorize(UUID orderId, BigDecimal amount) {
        String reference = "PAY-" + UUID.randomUUID();
        log.info("Dummy payment authorized: order={}, amount={}, reference={}", orderId, amount, reference);
        return reference;
    }

    @Override
    public void voidAuthorization(String reference) {
        log.info("Dummy payment authorization voided: reference={}", reference);
    }
}
