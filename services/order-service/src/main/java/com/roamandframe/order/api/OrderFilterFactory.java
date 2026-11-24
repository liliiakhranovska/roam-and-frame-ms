package com.roamandframe.order.api;

import com.roamandframe.order.model.OrderFilter;
import com.roamandframe.order.model.OrderSort;
import com.roamandframe.order.model.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** Validates query parameters and builds an {@link OrderFilter}; shared by the customer and admin endpoints. */
final class OrderFilterFactory {

    private static final int MAX_SKU_LENGTH = 64;

    private OrderFilterFactory() {
    }

    static Instant instant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }

    static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    static OrderFilter create(UUID customerId, Set<OrderStatus> statuses,
                              Instant createdFrom, Instant createdBefore,
                              Instant shippedFrom, Instant shippedBefore,
                              BigDecimal minTotal, BigDecimal maxTotal, String sku,
                              String sort, String direction) {
        requireOrdered(createdFrom, createdBefore, "createdFrom", "createdBefore");
        requireOrdered(shippedFrom, shippedBefore, "shippedFrom", "shippedBefore");
        if (minTotal != null && minTotal.signum() < 0) throw bad("minTotal must not be negative");
        if (maxTotal != null && maxTotal.signum() < 0) throw bad("maxTotal must not be negative");
        if (minTotal != null && maxTotal != null && minTotal.compareTo(maxTotal) > 0) {
            throw bad("minTotal must not be greater than maxTotal");
        }

        String cleanSku = sku == null || sku.isBlank() ? null : sku.trim();
        if (cleanSku != null && cleanSku.length() > MAX_SKU_LENGTH) {
            throw bad("sku must be at most " + MAX_SKU_LENGTH + " characters");
        }

        OrderSort sortBy;
        try {
            sortBy = OrderSort.from(sort);
        } catch (IllegalArgumentException e) {
            throw bad("sort must be one of: createdAt, total");
        }
        boolean descending = switch (direction.toLowerCase(Locale.ROOT)) {
            case "asc" -> false;
            case "desc" -> true;
            default -> throw bad("direction must be 'asc' or 'desc'");
        };

        Set<OrderStatus> cleanStatuses = statuses == null || statuses.isEmpty() ? null : statuses;
        return new OrderFilter(customerId, cleanStatuses, createdFrom, createdBefore,
                shippedFrom, shippedBefore, minTotal, maxTotal, cleanSku, sortBy, descending);
    }

    private static void requireOrdered(Instant from, Instant before, String fromName, String beforeName) {
        if (from != null && before != null && !before.isAfter(from)) {
            throw bad(beforeName + " must be after " + fromName);
        }
    }
}
