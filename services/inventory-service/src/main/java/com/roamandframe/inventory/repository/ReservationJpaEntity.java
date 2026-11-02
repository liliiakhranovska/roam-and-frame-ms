package com.roamandframe.inventory.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "reservations")
class ReservationJpaEntity {

    private static final String RESERVED = "RESERVED";
    private static final String RELEASED = "RELEASED";

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String status;

    protected ReservationJpaEntity() {
    }

    ReservationJpaEntity(UUID orderId, String sku, int quantity) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.sku = sku;
        this.quantity = quantity;
        this.status = RESERVED;
    }

    String getSku() {
        return sku;
    }

    int getQuantity() {
        return quantity;
    }

    boolean isReserved() {
        return RESERVED.equals(status);
    }

    void markReleased() {
        this.status = RELEASED;
    }
}
