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
class RestOrderClient implements OrderClient {

    private record CreateRequest(UUID orderId, UUID customerId, List<CartLine> items) {
    }

    private final RestClient restClient;
    private final CircuitBreaker breaker;

    RestOrderClient(@Value("${order.url}") String baseUrl,
                    @Value("${order.timeout}") Duration timeout,
                    CircuitBreakerRegistry registry) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
        this.breaker = registry.circuitBreaker("order");
    }

    @Override
    public PlacedOrder create(UUID orderId, UUID customerId, List<CartLine> items) {
        return ClientSupport.call(breaker, "Order", () -> restClient.post()
                .uri("/internal/orders")
                .body(new CreateRequest(orderId, customerId, items))
                .retrieve()
                .body(PlacedOrder.class));
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
