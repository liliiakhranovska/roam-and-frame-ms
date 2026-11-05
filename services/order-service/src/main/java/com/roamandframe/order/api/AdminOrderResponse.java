package com.roamandframe.order.api;

import com.roamandframe.order.model.Order;
import com.roamandframe.order.model.OrderDetails;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

record AdminOrderResponse(UUID id, UUID customerId, String status, BigDecimal total, Instant createdAt,
                          List<OrderItemResponse> items, ShipmentResponse shipment) {
    static AdminOrderResponse from(OrderDetails d) {
        Order o = d.order();
        return new AdminOrderResponse(o.id(), o.customerId(), o.status().name(), o.total(), o.createdAt(),
                o.items().stream().map(OrderItemResponse::from).toList(),
                ShipmentResponse.from(d.shipment()));
    }
}
