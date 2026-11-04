package com.roamandframe.cart.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cart_items")
class CartItemJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CartItemJpaEntity() {
    }

    CartItemJpaEntity(UUID customerId, String sku, int quantity) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.sku = sku;
        this.quantity = quantity;
        this.createdAt = Instant.now();
    }

    String getSku() { return sku; }
    int getQuantity() { return quantity; }

    void setQuantity(int quantity) { this.quantity = quantity; }
}
