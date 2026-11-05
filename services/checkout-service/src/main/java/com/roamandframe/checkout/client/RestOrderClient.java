package com.roamandframe.checkout.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Component
class RestOrderClient implements OrderClient {

    private record CreateRequest(UUID orderId, UUID customerId, List<CartLine> items) {
    }

    private final RestClient restClient;

    RestOrderClient(@Value("${order.url}") String baseUrl,
                    @Value("${order.timeout}") Duration timeout) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
    }

    @Override
    public PlacedOrder create(UUID orderId, UUID customerId, List<CartLine> items) {
        try {
            return restClient.post()
                    .uri("/internal/orders")
                    .body(new CreateRequest(orderId, customerId, items))
                    .retrieve()
                    .body(PlacedOrder.class);
        } catch (RestClientException e) {
            throw ClientSupport.failure("Order", e);
        }
    }

    @Override
    public void cancel(UUID orderId) {
        try {
            restClient.delete()
                    .uri("/internal/orders/{orderId}", orderId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw ClientSupport.failure("Order", e);
        }
    }
}
