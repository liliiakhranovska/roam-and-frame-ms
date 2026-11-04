package com.roamandframe.gateway.client;

public interface CustomerClient {
    /** Checks the credentials in the Customer service; throws InvalidCredentialsException if they are wrong. */
    AuthenticatedCustomer authenticate(String email, String password);
}
