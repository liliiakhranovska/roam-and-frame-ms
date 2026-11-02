package com.roamandframe.inventory.exception;

public class StockReservationException extends RuntimeException {

    public StockReservationException(String sku, int quantity) {
        super("Unable to reserve " + quantity + " of " + sku + ": not enough stock");
    }
}
