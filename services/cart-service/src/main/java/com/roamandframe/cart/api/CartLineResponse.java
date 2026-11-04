package com.roamandframe.cart.api;

import com.roamandframe.cart.model.CartLine;

import java.math.BigDecimal;

public record CartLineResponse(
        String sku, String name, BigDecimal unitPrice, int quantity,
        BigDecimal lineTotal, int availableQuantity
) {
    public static CartLineResponse from(CartLine l) {
        return new CartLineResponse(l.sku(), l.name(), l.unitPrice(), l.quantity(),
                l.lineTotal(), l.availableQuantity());
    }
}
