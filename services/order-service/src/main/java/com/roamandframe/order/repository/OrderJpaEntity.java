package com.roamandframe.order.repository;

import com.roamandframe.order.model.Order;
import com.roamandframe.order.model.OrderItem;
import com.roamandframe.order.model.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
class OrderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lineNo")
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    protected OrderJpaEntity() {
    }

    OrderJpaEntity(UUID id, UUID customerId, BigDecimal total, List<OrderItem> orderItems) {
        this.id = id;
        this.customerId = customerId;
        this.status = OrderStatus.PLACED;
        this.total = total;
        this.createdAt = Instant.now();
        for (int i = 0; i < orderItems.size(); i++) {
            items.add(new OrderItemJpaEntity(this, i + 1, orderItems.get(i)));
        }
    }

    Order toModel() {
        return new Order(id, customerId, status, total, createdAt, items.stream().map(OrderItemJpaEntity::toModel).toList());
    }

    boolean ship() {
        if (status == OrderStatus.SHIPPED) {
            return false;
        }
        status = OrderStatus.SHIPPED;
        return true;
    }

    void cancel() {
        status = OrderStatus.CANCELLED;
    }

    UUID getId() { return id; }
}
