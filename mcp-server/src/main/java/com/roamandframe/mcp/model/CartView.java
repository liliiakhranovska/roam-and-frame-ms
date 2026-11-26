package com.roamandframe.mcp.model;

import java.math.BigDecimal;
import java.util.List;

public record CartView(List<Item> items, BigDecimal total) {
    public record Item(String sku, String name, BigDecimal unitPrice, int quantity,
                       BigDecimal lineTotal, Integer availableQuantity) { }
}
