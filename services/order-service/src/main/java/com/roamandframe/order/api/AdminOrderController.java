package com.roamandframe.order.api;

import com.roamandframe.order.model.OrderFilter;
import com.roamandframe.order.model.OrderStatus;
import com.roamandframe.order.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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
            // legacy: UTC calendar dates, inclusive
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime shippedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime shippedBefore,
            @RequestParam(required = false) BigDecimal minTotal,
            @RequestParam(required = false) BigDecimal maxTotal,
            @RequestParam(required = false) String sku,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        if ((from != null || to != null) && (createdFrom != null || createdBefore != null)) {
            throw OrderFilterFactory.bad("use either from/to or createdFrom/createdBefore, not both");
        }
        if (from != null && to != null && to.isBefore(from)) {
            throw OrderFilterFactory.bad("'to' must not be before 'from'");
        }
        Instant createdFromInstant = from != null
                ? from.atStartOfDay(ZoneOffset.UTC).toInstant()
                : OrderFilterFactory.instant(createdFrom);
        Instant createdBeforeInstant = to != null
                ? to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
                : OrderFilterFactory.instant(createdBefore);

        OrderFilter filter = OrderFilterFactory.create(customerId, status,
                createdFromInstant, createdBeforeInstant,
                OrderFilterFactory.instant(shippedFrom), OrderFilterFactory.instant(shippedBefore),
                minTotal, maxTotal, sku, sort, direction);
        return orderService.searchOrders(filter).stream()
                .map(AdminOrderResponse::from)
                .toList();
    }

    @PostMapping("/{orderId}/ship")
    AdminOrderResponse shipOrder(@PathVariable UUID orderId) {
        return AdminOrderResponse.from(orderService.markShipped(orderId));
    }
}