package com.roamandframe.checkout.client;

import java.util.List;
import java.util.UUID;

public interface OrderClient {

    /** Creates the order as PLACED; a repeat returns the same order; refused (409) if it was cancelled. */
    PlacedOrder create(UUID orderId, UUID customerId, List<CartLine> items);

    /** Undo of create; safe to repeat. */
    void cancel(UUID orderId);
}