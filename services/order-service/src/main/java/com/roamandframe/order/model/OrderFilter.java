package com.roamandframe.order.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import java.math.BigDecimal;

public record OrderFilter(UUID customerId, Set<OrderStatus> statuses,
                          Instant createdFrom, Instant createdBefore,
                          Instant shippedFrom, Instant shippedBefore,
                          BigDecimal minTotal, BigDecimal maxTotal, String sku,
                          OrderSort sort, boolean descending) {
}
