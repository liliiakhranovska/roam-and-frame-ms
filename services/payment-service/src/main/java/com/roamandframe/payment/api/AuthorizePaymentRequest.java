package com.roamandframe.payment.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

record AuthorizePaymentRequest(
        @NotNull UUID orderId,
        @NotNull UUID customerId,
        @NotNull @DecimalMin("0.00") BigDecimal amount) {
}
