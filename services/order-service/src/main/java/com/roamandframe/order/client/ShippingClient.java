package com.roamandframe.order.client;

import com.roamandframe.order.model.ShipmentInfo;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface ShippingClient {
    Map<UUID, ShipmentInfo> findByOrderIds(Collection<UUID> orderIds);

    /** Marks the shipment as shipped (safe to repeat); throws ShipmentRejectedException if Shipping refuses. */
    ShipmentInfo ship(UUID orderId);
}
