package com.roamandframe.cart.exception;

public class CatalogUnavailableException extends RuntimeException {
    public CatalogUnavailableException(Throwable cause) { super("Catalog service is not available", cause); }
}
