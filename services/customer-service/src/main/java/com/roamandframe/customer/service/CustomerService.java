package com.roamandframe.customer.service;

import com.roamandframe.customer.model.AuthenticatedCustomer;
import com.roamandframe.customer.model.CustomerProfile;
import com.roamandframe.customer.model.UpdateProfileCommand;

import java.util.UUID;

public interface CustomerService {
    AuthenticatedCustomer authenticate(String email, String rawPassword);
    CustomerProfile getProfile(UUID customerId);
    CustomerProfile updateProfile(UUID customerId, UpdateProfileCommand command);
}
