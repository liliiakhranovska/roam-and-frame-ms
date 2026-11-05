package com.roamandframe.checkout.client;

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

    RestInventoryClient(@Value("${inventory.url}") String baseUrl,
                        @Value("${inventory.timeout}") Duration timeout) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
    }

    @Override
    public void reserve(UUID orderId, List<CartLine> items) {
        ReserveRequest request = new ReserveRequest(orderId,
                items.stream().map(i -> new Item(i.sku(), i.quantity())).toList());
        try {
            restClient.post()
                    .uri("/internal/inventory/reservations")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw ClientSupport.failure("Inventory", e);
        }
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

