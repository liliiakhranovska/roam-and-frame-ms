package com.roamandframe.order.service;

import com.roamandframe.order.model.Order;
import com.roamandframe.order.model.OrderDetails;
import com.roamandframe.order.model.OrderFilter;
import com.roamandframe.order.model.OrderItem;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order create(UUID orderId, UUID customerId, List<OrderItem> items);
    void cancel(UUID orderId);
    List<OrderDetails> getOrders(UUID customerId);
    OrderDetails markShipped(UUID orderId);
    List<OrderDetails> searchOrders(OrderFilter filter);
}
