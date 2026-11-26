package com.roamandframe.mcp.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderView(UUID id, String status, BigDecimal total, Instant createdAt, Instant shippedAt,
                        List<Item> items, Shipment shipment) {

    public record Item(String sku, String name, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) { }

    public record Shipment(String status, String trackingNumber) { }
}
