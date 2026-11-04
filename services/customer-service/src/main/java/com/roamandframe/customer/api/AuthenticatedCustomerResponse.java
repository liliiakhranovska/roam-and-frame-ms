package com.roamandframe.customer.api;

import com.roamandframe.customer.model.AuthenticatedCustomer;
import com.roamandframe.customer.model.CustomerRole;

import java.util.UUID;

record AuthenticatedCustomerResponse(UUID customerId, String email, CustomerRole role) {
    static AuthenticatedCustomerResponse from(AuthenticatedCustomer c) {
        return new AuthenticatedCustomerResponse(c.id(), c.email(), c.role());
    }
}