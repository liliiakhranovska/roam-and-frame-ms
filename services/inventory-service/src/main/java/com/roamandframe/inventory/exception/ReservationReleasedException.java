package com.roamandframe.inventory.exception;

import java.util.UUID;

public class ReservationReleasedException extends RuntimeException {

    public ReservationReleasedException(UUID orderId) {
        super("The reservation for order " + orderId + " was already released");
    }
}