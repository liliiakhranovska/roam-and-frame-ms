package com.roamandframe.shipping.gateway;

import com.roamandframe.shipping.model.DeliveryAddress;

import java.util.UUID;

/** Port to the external carrier. */
public interface ShippingGateway {

    /** Books the shipment and returns the carrier's tracking number. */
    String createShipment(UUID orderId, DeliveryAddress address);
    void cancelShipment(String trackingNumber);
}
