package com.roamandframe.order.model;

import java.util.Locale;

public enum OrderSort {
    CREATED_AT, TOTAL;

    /** Accepts "createdAt" and "total" (case-insensitive); anything else is rejected. */
    public static OrderSort from(String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "createdat" -> CREATED_AT;
            case "total" -> TOTAL;
            default -> throw new IllegalArgumentException("Unknown sort: " + value);
        };
    }
}
