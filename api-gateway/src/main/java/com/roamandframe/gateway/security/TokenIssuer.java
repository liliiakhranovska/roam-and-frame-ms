package com.roamandframe.gateway.security;

import com.roamandframe.gateway.client.AuthenticatedCustomer;

public interface TokenIssuer {
    String issueToken(AuthenticatedCustomer customer);
}
