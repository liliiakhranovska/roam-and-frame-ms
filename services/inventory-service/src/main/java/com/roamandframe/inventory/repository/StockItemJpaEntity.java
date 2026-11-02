package com.roamandframe.inventory.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_items")
class StockItemJpaEntity {

    @Id
    private String sku;

    @Column(nullable = false)
    private int quantity;

    protected StockItemJpaEntity() {
    }

    String getSku() {
        return sku;
    }

    int getQuantity() {
        return quantity;
    }
}
