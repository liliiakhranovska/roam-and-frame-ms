package com.roamandframe.checkout.client;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PlacedOrder(UUID id, String status, BigDecimal total, Instant createdAt, List<CartLine> items) {
}