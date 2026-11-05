package com.roamandframe.checkout.repository;

import com.roamandframe.checkout.model.Checkout;
import com.roamandframe.checkout.model.CheckoutStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckoutRepository {

    /** Records a new RUNNING checkout; throws CheckoutInProgressException if the customer has an unfinished one. */
    Checkout start(UUID id, UUID customerId);

    Optional<Checkout> findById(UUID id);

    /** Saves the last completed step and refreshes the progress time. */
    void recordStep(UUID id, int step);

    void setStatus(UUID id, CheckoutStatus status, String failureReason);

    /** Unfinished checkouts without progress since the given time. */
    List<Checkout> findStale(Instant before);

    /** Takes over a stale checkout; true for exactly one caller even if several try at once. */
    boolean claim(Checkout stale);
}
