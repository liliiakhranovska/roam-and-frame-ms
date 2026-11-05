package com.roamandframe.checkout.client;

import java.util.UUID;

public interface CartClient {

    /** Checks the cart without changing it; refused (409) if it is empty or a line is invalid. */
    CartContents validate(UUID customerId);

    /** Saga step 7; safe to repeat. */
    void clear(UUID customerId);
}
