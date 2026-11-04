package com.roamandframe.cart.api;

import com.roamandframe.cart.model.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(List<CartLineResponse> items, BigDecimal total) {
    public static CartResponse from(Cart c) {
        return new CartResponse(c.lines().stream().map(CartLineResponse::from).toList(), c.total());
    }
}

