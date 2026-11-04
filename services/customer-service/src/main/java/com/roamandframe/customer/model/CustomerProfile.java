package com.roamandframe.customer.model;

import java.util.UUID;

public record CustomerProfile(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String addressLine1,
        String addressLine2,
        String city,
        String region,
        String postalCode,
        String countryCode
) {
}
