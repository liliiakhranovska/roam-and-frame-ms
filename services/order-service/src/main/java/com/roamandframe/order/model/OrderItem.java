package com.roamandframe.order.model;

import java.math.BigDecimal;

public record OrderItem(String sku, String name, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {
}