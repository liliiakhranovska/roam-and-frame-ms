package com.roamandframe.cart.exception;

import java.util.List;

public class InvalidCartException extends RuntimeException {
    public InvalidCartException(List<String> problems) {
        super("Cart cannot be checked out: " + String.join("; ", problems));
    }
}
