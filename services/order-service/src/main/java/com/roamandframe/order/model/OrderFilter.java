package com.roamandframe.order.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record OrderFilter(UUID customerId, Set<OrderStatus> statuses, Instant createdFrom, Instant createdBefore) {
}
