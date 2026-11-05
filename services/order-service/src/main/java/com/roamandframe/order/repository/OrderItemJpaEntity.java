package com.roamandframe.order.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
class OrderItemJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private OrderJpaEntity order;

    @Column(name = "line_no", nullable = false)
    private int lineNo;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "line_total", nullable = false)
    private BigDecimal lineTotal;

    protected OrderItemJpaEntity() {
    }

    OrderItemJpaEntity(OrderJpaEntity order, int lineNo, OrderItem item) {
        this.id = UUID.randomUUID();
        this.order = order;
        this.lineNo = lineNo;
        this.sku = item.sku();
        this.name = item.name();
        this.unitPrice = item.unitPrice();
        this.quantity = item.quantity();
        this.lineTotal = item.lineTotal();
    }

    OrderItem toModel() {
        return new OrderItem(sku, name, unitPrice, quantity, lineTotal);
    }
}
