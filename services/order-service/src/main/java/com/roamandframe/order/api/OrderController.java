package com.roamandframe.order.api;

import com.roamandframe.order.model.OrderFilter;
import com.roamandframe.order.model.OrderStatus;
import com.roamandframe.order.security.CurrentCustomerId;
import com.roamandframe.order.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
class OrderController {

    /** CANCELLED orders belong to failed checkouts and are never shown to customers. */
    private static final Set<OrderStatus> VISIBLE = Set.of(OrderStatus.PLACED, OrderStatus.SHIPPED);

    private final OrderService orderService;

    OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    List<OrderResponse> getOrders(
            @CurrentCustomerId UUID customerId,
            @RequestParam(required = false) Set<OrderStatus> status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime shippedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime shippedBefore,
            @RequestParam(required = false) BigDecimal minTotal,
            @RequestParam(required = false) BigDecimal maxTotal,
            @RequestParam(required = false) String sku,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        Set<OrderStatus> statuses = status == null || status.isEmpty() ? VISIBLE : status;
        if (statuses.contains(OrderStatus.CANCELLED)) {
            throw OrderFilterFactory.bad("status must be PLACED or SHIPPED");
        }
        OrderFilter filter = OrderFilterFactory.create(customerId, statuses,
                OrderFilterFactory.instant(createdFrom), OrderFilterFactory.instant(createdBefore),
                OrderFilterFactory.instant(shippedFrom), OrderFilterFactory.instant(shippedBefore),
                minTotal, maxTotal, sku, sort, direction);
        return orderService.searchOrders(filter).stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/{orderId}")
    OrderResponse getOrder(@CurrentCustomerId UUID customerId, @PathVariable UUID orderId) {
        return OrderResponse.from(orderService.getOrder(customerId, orderId));
    }
}