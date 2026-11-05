package com.roamandframe.order.api;

import com.roamandframe.order.model.Order;
import com.roamandframe.order.model.OrderDetails;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

record OrderResponse(UUID id, String status, BigDecimal total, Instant createdAt,
                     List<OrderItemResponse> items, ShipmentResponse shipment) {
    static OrderResponse from(OrderDetails d) {
        Order o = d.order();
        return new OrderResponse(o.id(), o.status().name(), o.total(), o.createdAt(),
                o.items().stream().map(OrderItemResponse::from).toList(),
                ShipmentResponse.from(d.shipment()));
    }
}
