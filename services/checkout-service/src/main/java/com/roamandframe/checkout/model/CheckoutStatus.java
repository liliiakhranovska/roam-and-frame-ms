package com.roamandframe.checkout.model;

public enum CheckoutStatus {
    RUNNING,        // steps 1-6 in progress
    COMPENSATING,   // a step failed; completed steps are being undone
    FINISHING,      // steps 1-6 done; the cart clear (step 7) is still to do
    COMPLETED,
    FAILED          // everything undone
}
