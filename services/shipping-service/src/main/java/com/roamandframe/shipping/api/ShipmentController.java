package com.roamandframe.shipping.api;

import com.roamandframe.shipping.service.ShippingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/shipments")
class ShipmentController {

    private final ShippingService shippingService;

    ShipmentController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping
    ShipmentResponse create(@Valid @RequestBody CreateShipmentRequest request) {
        return ShipmentResponse.from(shippingService.create(
                request.orderId(), request.customerId(), request.address().toModel()));
    }

    @GetMapping
    List<ShipmentResponse> find(@RequestParam List<UUID> orderIds) {
        return shippingService.findByOrderIds(orderIds).values().stream()
                .map(ShipmentResponse::from)
                .toList();
    }

    @PostMapping("/{orderId}/ship")
    ShipmentResponse ship(@PathVariable UUID orderId) {
        return ShipmentResponse.from(shippingService.markShipped(orderId));
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancel(@PathVariable UUID orderId) {
        shippingService.cancel(orderId);
    }
}
