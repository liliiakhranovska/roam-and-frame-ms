package com.roamandframe.checkout.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.UUID;

@Component
class RestCartClient implements CartClient {

    private final RestClient restClient;
    private final CircuitBreaker breaker;

    RestCartClient(@Value("${cart.url}") String baseUrl,
                   @Value("${cart.timeout}") Duration timeout,
                    CircuitBreakerRegistry registry) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
        this.breaker = registry.circuitBreaker("cart");
    }

    @Override
    public CartContents validate(UUID customerId) {
        return ClientSupport.call(breaker, "Cart", () -> restClient.post()
                .uri("/internal/carts/{customerId}/validation", customerId)
                .retrieve()
                .body(CartContents.class));
    }

    @Override
    public void clear(UUID customerId) {
        ClientSupport.run(breaker, "Cart", () -> restClient.delete()
                .uri("/internal/carts/{customerId}", customerId)
                .retrieve()
                .toBodilessEntity());
    }
}
