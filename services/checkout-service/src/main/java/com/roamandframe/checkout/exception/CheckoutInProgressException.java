package com.roamandframe.checkout.exception;

import java.util.UUID;

public class CheckoutInProgressException extends RuntimeException {
    public CheckoutInProgressException(UUID customerId) {
        super("A checkout for customer " + customerId + " is already in progress");
    }
}