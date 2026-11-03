package com.roamandframe.catalog.client;

public class InventoryUnavailableException extends RuntimeException {
    public InventoryUnavailableException(Throwable cause) {
        super("Inventory service is not available", cause);
    }
}
