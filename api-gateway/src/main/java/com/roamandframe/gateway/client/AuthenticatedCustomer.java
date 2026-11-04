package com.roamandframe.gateway.client;

import java.util.UUID;

public record AuthenticatedCustomer(UUID customerId, String email, String role) { }
