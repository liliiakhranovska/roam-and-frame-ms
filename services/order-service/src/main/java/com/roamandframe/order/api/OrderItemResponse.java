package com.roamandframe.order.api;

import com.roamandframe.order.model.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(String sku, String name, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {
    public static OrderItemResponse from(OrderItem i) {
        return new OrderItemResponse(i.sku(), i.name(), i.unitPrice(), i.quantity(), i.lineTotal());
    }
}
