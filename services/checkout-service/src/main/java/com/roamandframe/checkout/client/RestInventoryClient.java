package com.roamandframe.checkout.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Component
class RestInventoryClient implements InventoryClient {

    private record Item(String sku, int quantity) {
    }

    private record ReserveRequest(UUID orderId, List<Item> items) {
    }

    private final RestClient restClient;
    private final CircuitBreaker breaker;

    RestInventoryClient(@Value("${inventory.url}") String baseUrl,
                        @Value("${inventory.timeout}") Duration timeout,
                        CircuitBreakerRegistry registry) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
        this.breaker = registry.circuitBreaker("inventory");
    }

    @Override
    public void reserve(UUID orderId, List<CartLine> items) {
        ReserveRequest request = new ReserveRequest(orderId,
                items.stream().map(i -> new Item(i.sku(), i.quantity())).toList());
        ClientSupport.run(breaker, "Inventory", () -> restClient.post()
                .uri("/internal/inventory/reservations")
                .body(request)
                .retrieve()
                .toBodilessEntity());
    }

    @Override
    public void release(UUID orderId) {
        try {
            restClient.delete()
                    .uri("/internal/inventory/reservations/{orderId}", orderId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw ClientSupport.failure("Inventory", e);
        }
    }
}

