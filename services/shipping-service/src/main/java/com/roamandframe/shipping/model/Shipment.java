package com.roamandframe.shipping.model;

import java.util.UUID;

public record Shipment(UUID id, UUID orderId, UUID customerId, ShipmentStatus status, String trackingNumber) {
}
