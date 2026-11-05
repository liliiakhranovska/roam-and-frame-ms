package com.roamandframe.order.exception;

import java.util.UUID;

public class OrderCancelledException extends RuntimeException {
    public OrderCancelledException(UUID orderId) { super("Order " + orderId + " was cancelled"); }
}
