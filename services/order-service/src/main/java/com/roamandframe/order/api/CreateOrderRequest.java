package com.roamandframe.order.api;

import com.roamandframe.order.model.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

record CreateOrderRequest(
        @NotNull UUID orderId,
        @NotNull UUID customerId,
        @NotEmpty @Valid List<Item> items) {

    record Item(@NotBlank String sku, @NotBlank String name,
                @NotNull @PositiveOrZero BigDecimal unitPrice, @Positive int quantity,
                @NotNull @PositiveOrZero BigDecimal lineTotal) {
        OrderItem toModel() {
            return new OrderItem(sku, name, unitPrice, quantity, lineTotal);
        }
    }
}
