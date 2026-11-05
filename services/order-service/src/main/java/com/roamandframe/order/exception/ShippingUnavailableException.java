package com.roamandframe.order.exception;

public class ShippingUnavailableException extends RuntimeException {
    public ShippingUnavailableException(Throwable cause) { super("Shipping service is not available", cause); }
}
