package com.roamandframe.customer.model;

public record UpdateProfileCommand(
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

