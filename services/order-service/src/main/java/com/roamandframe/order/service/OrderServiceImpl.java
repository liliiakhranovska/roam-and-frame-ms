package com.roamandframe.order.service;

import com.roamandframe.order.client.ShippingClient;
import com.roamandframe.order.exception.OrderAlreadyShippedException;
import com.roamandframe.order.exception.OrderCancelledException;
import com.roamandframe.order.exception.OrderNotFoundException;
import com.roamandframe.order.model.*;
import com.roamandframe.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ShippingClient shippingClient;

    public OrderServiceImpl(OrderRepository orderRepository, ShippingClient shippingClient) {
        this.orderRepository = orderRepository;
        this.shippingClient = shippingClient;
    }

    @Override
    public Order create(UUID orderId, UUID customerId, List<OrderItem> items) {
        Optional<Order> existing = orderRepository.findById(orderId);
        if (existing.isPresent()) {
            if (existing.get().status() == OrderStatus.CANCELLED) {
                throw new OrderCancelledException(orderId);
            }
            return existing.get(); // a retry of a call that already succeeded
        }
        BigDecimal total = items.stream().map(OrderItem::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return orderRepository.create(orderId, customerId, items, total);
    }

    @Override
    public void cancel(UUID orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.status() == OrderStatus.SHIPPED) {
                throw new OrderAlreadyShippedException(orderId);
            }
            if (order.status() == OrderStatus.PLACED) {
                orderRepository.markCancelled(orderId);
            }
        });
    }

    @Override
    public OrderDetails markShipped(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.status() == OrderStatus.CANCELLED) {
            throw new OrderCancelledException(orderId);
        }
        ShipmentInfo shipment = shippingClient.ship(orderId);   // first: safe to repeat
        orderRepository.markShipped(orderId);
        Order shipped = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return new OrderDetails(shipped, shipment);
    }

    @Override
    public List<OrderDetails> searchOrders(OrderFilter filter) {
        return withShipments(orderRepository.search(filter));
    }

    private List<OrderDetails> withShipments(List<Order> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }
        Map<UUID, ShipmentInfo> shipments = shippingClient.findByOrderIds(orders.stream().map(Order::id).toList());
        return orders.stream().map(o -> new OrderDetails(o, shipments.get(o.id()))).toList();
    }

    @Override
    public OrderDetails getOrder(UUID customerId, UUID orderId) {
        // 404 (not 403) for someone else's or a cancelled order: never reveal that it exists
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.customerId().equals(customerId))
                .filter(o -> o.status() != OrderStatus.CANCELLED)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return withShipments(List.of(order)).get(0);
    }
}
