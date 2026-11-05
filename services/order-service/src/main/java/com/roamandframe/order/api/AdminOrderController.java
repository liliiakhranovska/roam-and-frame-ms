package com.roamandframe.order.api;

import com.roamandframe.order.model.OrderFilter;
import com.roamandframe.order.model.OrderStatus;
import com.roamandframe.order.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/orders")
class AdminOrderController {

    private final OrderService orderService;

    AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    List<AdminOrderResponse> searchOrders(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) Set<OrderStatus> status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from != null && to != null && to.isBefore(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'to' must not be before 'from'");
        }
        Instant createdFrom = from == null ? null : from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant createdBefore = to == null ? null : to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return orderService.searchOrders(new OrderFilter(customerId, status, createdFrom, createdBefore)).stream()
                .map(AdminOrderResponse::from)
                .toList();
    }

    @PostMapping("/{orderId}/ship")
    AdminOrderResponse shipOrder(@PathVariable UUID orderId) {
        return AdminOrderResponse.from(orderService.markShipped(orderId));
    }
}