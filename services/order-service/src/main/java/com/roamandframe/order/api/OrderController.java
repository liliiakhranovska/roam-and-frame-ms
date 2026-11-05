package com.roamandframe.order.api;

import com.roamandframe.order.security.CurrentCustomerId;
import com.roamandframe.order.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
class OrderController {

    private final OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    List<OrderResponse> getOrders(@CurrentCustomerId UUID customerId) {
        return orderService.getOrders(customerId).stream().map(OrderResponse::from).toList();
    }
}
