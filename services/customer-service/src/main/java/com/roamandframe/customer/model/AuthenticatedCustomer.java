package com.roamandframe.customer.model;

import java.util.UUID;

public record AuthenticatedCustomer(UUID id, String email, CustomerRole role) {
}
