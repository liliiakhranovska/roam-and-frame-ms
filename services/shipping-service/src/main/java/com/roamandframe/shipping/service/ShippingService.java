package com.roamandframe.shipping.service;

import com.roamandframe.shipping.model.DeliveryAddress;
import com.roamandframe.shipping.model.Shipment;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface ShippingService {
    Shipment create(UUID orderId, UUID customerId, DeliveryAddress address);
    Map<UUID, Shipment> findByOrderIds(Collection<UUID> orderIds);
    Shipment markShipped(UUID orderId);
    void cancel(UUID orderId);
}
