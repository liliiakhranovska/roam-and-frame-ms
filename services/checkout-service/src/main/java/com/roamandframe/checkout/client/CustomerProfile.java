package com.roamandframe.checkout.client;

import java.util.UUID;

public record CustomerProfile(
        UUID id, String email, String firstName, String lastName,
        String addressLine1, String addressLine2, String city, String region,
        String postalCode, String countryCode
) {
}
