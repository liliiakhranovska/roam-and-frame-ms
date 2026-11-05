package com.roamandframe.shipping.repository;
import com.roamandframe.shipping.model.ShipmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shipments")
class ShipmentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @Column(name = "tracking_number", nullable = false)
    private String trackingNumber;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ShipmentJpaEntity() {
    }

    ShipmentJpaEntity(UUID orderId, UUID customerId, String trackingNumber) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = ShipmentStatus.INITIATED;
        this.trackingNumber = trackingNumber;
        this.createdAt = Instant.now();
    }

    UUID getId() { return id; }
    UUID getOrderId() { return orderId; }
    UUID getCustomerId() { return customerId; }
    ShipmentStatus getStatus() { return status; }
    String getTrackingNumber() { return trackingNumber; }
    void ship() {
        this.status = ShipmentStatus.SHIPPED;
    }
    void cancel() {
        this.status = ShipmentStatus.CANCELLED;
    }
}

