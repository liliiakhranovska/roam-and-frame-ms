package com.roamandframe.order.exception;

import java.util.UUID;

public class ShipmentRejectedException extends RuntimeException {
    public ShipmentRejectedException(UUID orderId) { super("Shipping refused to ship order " + orderId); }
}
