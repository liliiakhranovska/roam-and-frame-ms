package com.roamandframe.notification.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderConfirmationRequest(
        @NotNull @Valid Customer customer,
        @NotNull @Valid Order order,
        @NotNull @Valid Payment payment,
        String trackingNumber) {

    public record Customer(
            @NotBlank String firstName,
            @NotBlank @Email String email,
            @NotBlank String addressLine1,
            String addressLine2,
            @NotBlank String postalCode,
            @NotBlank String city,
            @NotBlank String countryCode) {
    }

    public record Order(
            @NotNull UUID id,
            @NotNull Instant createdAt,
            @NotNull BigDecimal total,
            @NotEmpty List<@Valid Item> items) {
    }

    public record Item(
            @NotBlank String name,
            @Positive int quantity,
            @NotNull BigDecimal lineTotal) {
    }

    public record Payment(
            @NotBlank String status,
            @NotBlank String reference) {
    }
}
