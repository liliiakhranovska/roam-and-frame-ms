package com.roamandframe.mcp.model;

import java.util.UUID;

public record ProfileView(UUID id, String email, String firstName, String lastName, String phone,
                          String addressLine1, String addressLine2, String city, String region,
                          String postalCode, String countryCode) { }
