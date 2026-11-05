package com.roamandframe.checkout.repository;

import com.roamandframe.checkout.model.Checkout;
import com.roamandframe.checkout.model.CheckoutStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "checkouts")
class CheckoutJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckoutStatus status;

    @Column(name = "last_step", nullable = false)
    private int lastStep;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CheckoutJpaEntity() {
    }

    CheckoutJpaEntity(UUID id, UUID customerId) {
        this.id = id;
        this.customerId = customerId;
        this.status = CheckoutStatus.RUNNING;
        this.lastStep = 0;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    Checkout toModel() {
        return new Checkout(id, customerId, status, lastStep, failureReason, createdAt, updatedAt);
    }
}