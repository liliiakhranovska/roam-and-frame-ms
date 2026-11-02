package com.roamandframe.inventory.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

public record ReserveStockRequest(
        @NotNull UUID orderId,
        @NotEmpty List<@Valid Item> items) {

    public record Item(
            @NotBlank String sku,
            @Positive int quantity) {
    }
}
