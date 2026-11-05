package com.roamandframe.shipping.model;

public record DeliveryAddress(String addressLine1, String addressLine2, String city,
                              String region, String postalCode, String countryCode) {
}
