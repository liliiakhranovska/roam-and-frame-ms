package com.roamandframe.customer.api;

import com.roamandframe.customer.model.CustomerProfile;

import java.util.UUID;

public record ProfileResponse(
        UUID id, String email, String firstName, String lastName, String phone,
        String addressLine1, String addressLine2, String city, String region,
        String postalCode, String countryCode
) {
    public static ProfileResponse from(CustomerProfile p) {
        return new ProfileResponse(
                p.id(), p.email(), p.firstName(), p.lastName(), p.phone(),
                p.addressLine1(), p.addressLine2(), p.city(), p.region(),
                p.postalCode(), p.countryCode());
    }
}
