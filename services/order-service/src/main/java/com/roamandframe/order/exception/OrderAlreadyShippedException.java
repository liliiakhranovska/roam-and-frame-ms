package com.roamandframe.order.exception;

import java.util.UUID;

public class OrderAlreadyShippedException extends RuntimeException {
    public OrderAlreadyShippedException(UUID orderId) { super("Order " + orderId + " is already shipped"); }
}
