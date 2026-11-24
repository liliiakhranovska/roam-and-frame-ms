package com.roamandframe.catalog.model;

import java.util.Locale;

public enum ProductSort {
    NAME, PRICE, CREATED_AT;

    /** Accepts "name", "price", "createdAt" (case-insensitive); anything else is rejected. */
    public static ProductSort from(String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "name" -> NAME;
            case "price" -> PRICE;
            case "createdat" -> CREATED_AT;
            default -> throw new IllegalArgumentException("Unknown sort: " + value);
        };
    }
}
