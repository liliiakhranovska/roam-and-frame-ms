package com.roamandframe.checkout.service;

import com.roamandframe.checkout.model.Checkout;

import java.util.UUID;

public interface CheckoutService {

    /** Runs the whole saga. Throws StepRejectedException / StepUnavailableException after undoing what was done. */
    CheckoutResult checkout(UUID customerId);

    /** Finishes a stale checkout: undoes it (RUNNING, COMPENSATING) or clears the cart (FINISHING). */
    void recover(Checkout stale);
}
