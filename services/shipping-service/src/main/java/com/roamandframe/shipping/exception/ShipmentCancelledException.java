package com.roamandframe.shipping.exception;

import java.util.UUID;

public class ShipmentCancelledException extends RuntimeException {
    public ShipmentCancelledException(UUID orderId) { super("Shipment for order " + orderId + " was cancelled"); }
}
