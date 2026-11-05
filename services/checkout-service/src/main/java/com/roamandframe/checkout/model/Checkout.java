package com.roamandframe.checkout.model;

import java.time.Instant;
import java.util.UUID;

public record Checkout(UUID id, UUID customerId, CheckoutStatus status, int lastStep,
                       String failureReason, Instant createdAt, Instant updatedAt) {
}
