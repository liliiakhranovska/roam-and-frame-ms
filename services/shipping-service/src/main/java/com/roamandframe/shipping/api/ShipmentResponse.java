package com.roamandframe.shipping.api;

import com.roamandframe.shipping.model.Shipment;
import com.roamandframe.shipping.model.ShipmentStatus;

import java.util.UUID;

record ShipmentResponse(UUID orderId, ShipmentStatus status, String trackingNumber) {
    static ShipmentResponse from(Shipment s) {
        return new ShipmentResponse(s.orderId(), s.status(), s.trackingNumber());
    }
}
