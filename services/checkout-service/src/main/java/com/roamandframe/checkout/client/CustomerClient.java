package com.roamandframe.checkout.client;

import java.util.UUID;

public interface CustomerClient {

    CustomerProfile getProfile(UUID customerId);
}

