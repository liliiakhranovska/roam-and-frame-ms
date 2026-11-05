package com.roamandframe.checkout.client;

import java.util.UUID;

public record ShipmentResult(UUID orderId, String status, String trackingNumber) {
}
