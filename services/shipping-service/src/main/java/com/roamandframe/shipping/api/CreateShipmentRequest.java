package com.roamandframe.shipping.api;

import com.roamandframe.shipping.model.DeliveryAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

record CreateShipmentRequest(
        @NotNull UUID orderId,
        @NotNull UUID customerId,
        @NotNull @Valid Address address) {

    record Address(
            @NotBlank String addressLine1,
            String addressLine2,
            @NotBlank String city,
            String region,
            @NotBlank String postalCode,
            @NotBlank String countryCode) {

        DeliveryAddress toModel() {
            return new DeliveryAddress(addressLine1, addressLine2, city, region, postalCode, countryCode);
        }
    }
}
