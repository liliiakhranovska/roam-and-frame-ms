package com.roamandframe.order.repository;

import com.roamandframe.order.model.Order;
import com.roamandframe.order.model.OrderFilter;
import com.roamandframe.order.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order create(UUID id, UUID customerId, List<OrderItem> items, BigDecimal total);
    Optional<Order> findById(UUID id);
    /** CANCELLED orders belong to failed checkouts and are not listed. */
    List<Order> findByCustomerId(UUID customerId);
    void markShipped(UUID id);
    void markCancelled(UUID id);
    List<Order> search(OrderFilter filter);
}
