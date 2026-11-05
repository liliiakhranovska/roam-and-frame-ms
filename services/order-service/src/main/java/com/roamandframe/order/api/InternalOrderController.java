package com.roamandframe.order.api;

import com.roamandframe.order.model.OrderDetails;
import com.roamandframe.order.model.OrderItem;
import com.roamandframe.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/orders")
class InternalOrderController {

    private final OrderService orderService;

    InternalOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream().map(CreateOrderRequest.Item::toModel).toList();
        return OrderResponse.from(new OrderDetails(
                orderService.create(request.orderId(), request.customerId(), items), null));
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancel(@PathVariable UUID orderId) {
        orderService.cancel(orderId);
    }
}
