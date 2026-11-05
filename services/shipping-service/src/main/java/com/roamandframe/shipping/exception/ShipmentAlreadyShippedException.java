package com.roamandframe.shipping.exception;

import java.util.UUID;

public class ShipmentAlreadyShippedException extends RuntimeException {
    public ShipmentAlreadyShippedException(UUID orderId) { super("Shipment for order " + orderId + " is already shipped"); }
}
