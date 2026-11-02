package com.roamandframe.inventory.api;

import com.roamandframe.inventory.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/inventory")
class InventoryController {

    private final InventoryService inventoryService;

    InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/quantities")
    Map<String, Integer> quantities(@RequestParam List<String> skus) {
        return inventoryService.getQuantities(skus);
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void reserve(@Valid @RequestBody ReserveStockRequest request) {
        Map<String, Integer> quantities = request.items().stream()
                .collect(Collectors.toMap(ReserveStockRequest.Item::sku,
                        ReserveStockRequest.Item::quantity, Integer::sum));
        inventoryService.reserve(request.orderId(), quantities);
    }

    @DeleteMapping("/reservations/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void release(@PathVariable UUID orderId) {
        inventoryService.release(orderId);
    }
}
