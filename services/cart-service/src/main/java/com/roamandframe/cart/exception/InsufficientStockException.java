package com.roamandframe.cart.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String sku, int requested, int available) {
        super("Insufficient stock for " + sku + ": cart would contain " + requested
                + ", available " + available);
    }
}
