package com.roamandframe.order.api;

import com.roamandframe.order.model.ShipmentInfo;

record ShipmentResponse(String status, String trackingNumber) {
    static ShipmentResponse from(ShipmentInfo s) {
        return s == null ? null : new ShipmentResponse(s.status(), s.trackingNumber());
    }
}
