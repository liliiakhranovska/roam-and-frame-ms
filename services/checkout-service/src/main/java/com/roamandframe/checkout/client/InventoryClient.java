package com.roamandframe.checkout.client;

import java.util.List;
import java.util.UUID;

public interface InventoryClient {

    /** All or nothing; refused (409) if stock is short. A repeat with the same orderId is a no-op. */
    void reserve(UUID orderId, List<CartLine> items);

    /** Undo of reserve; safe to repeat. */
    void release(UUID orderId);
}