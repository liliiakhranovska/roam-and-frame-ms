package com.roamandframe.shipping.repository;

import com.roamandframe.shipping.model.Shipment;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository {
    Shipment create(UUID orderId, UUID customerId, String trackingNumber);
    Optional<Shipment> findByOrderId(UUID orderId);
    List<Shipment> findByOrderIds(Collection<UUID> orderIds);
    void markShipped(UUID orderId);
    void markCancelled(UUID orderId);
}
