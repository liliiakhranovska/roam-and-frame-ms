package com.roamandframe.cart.model;

import java.math.BigDecimal;

/** name, unitPrice and lineTotal are null when the product no longer exists in the catalog. */
public record CartLine(String sku, String name, BigDecimal unitPrice, int quantity,
                       BigDecimal lineTotal, int availableQuantity) {
}