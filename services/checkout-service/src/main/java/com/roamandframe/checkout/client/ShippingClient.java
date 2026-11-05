package com.roamandframe.checkout.client;

import java.util.UUID;

public interface ShippingClient {

    /** Books the carrier; a repeat returns the same shipment; refused (409) if it was cancelled. */
    ShipmentResult create(UUID orderId, CustomerProfile customer);

    /** Undo of create (cancels at the carrier too); safe to repeat. */
    void cancel(UUID orderId);
}
