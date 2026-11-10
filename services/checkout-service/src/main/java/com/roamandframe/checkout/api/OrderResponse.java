package com.roamandframe.checkout.api;

import com.roamandframe.checkout.service.CheckoutResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

record OrderResponse(UUID id, String status, BigDecimal total, Instant createdAt,
                     List<Item> items, Shipment shipment) {

    record Item(String sku, String name, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {
    }

    record Shipment(String status, String trackingNumber) {
    }

    static OrderResponse from(CheckoutResult result) {
        var order = result.order();
        var shipment = result.shipment();
        return new OrderResponse(order.id(), order.status(), order.total(), order.createdAt(),
                order.items().stream()
                        .map(i -> new Item(i.sku(), i.name(), i.unitPrice(), i.quantity(), i.lineTotal()))
                        .toList(),
                new Shipment(shipment.status(), shipment.trackingNumber()));
    }
}
