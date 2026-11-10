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
class RestCustomerClient implements CustomerClient {

    private final RestClient restClient;
    private final CircuitBreaker breaker;

    RestCustomerClient(@Value("${customer.url}") String baseUrl,
                       @Value("${customer.timeout}") Duration timeout,
                       CircuitBreakerRegistry registry) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
        this.breaker = registry.circuitBreaker("customer");
    }

    @Override
    public CustomerProfile getProfile(UUID customerId) {
        return ClientSupport.call(breaker, "Customer", () -> restClient.get()
                .uri("/internal/customers/{id}", customerId)
                .retrieve()
                .body(CustomerProfile.class));
    }
}
