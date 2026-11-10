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
class RestShippingClient implements ShippingClient {

    private record Address(String addressLine1, String addressLine2, String city, String region,
                           String postalCode, String countryCode) {
    }

    private record CreateRequest(UUID orderId, UUID customerId, Address address) {
    }

    private final RestClient restClient;
    private final CircuitBreaker breaker;

    RestShippingClient(@Value("${shipping.url}") String baseUrl,
                       @Value("${shipping.timeout}") Duration timeout,
                       CircuitBreakerRegistry registry) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
        this.breaker = registry.circuitBreaker("shipping");
    }

    @Override
    public ShipmentResult create(UUID orderId, CustomerProfile customer) {
        Address address = new Address(customer.addressLine1(), customer.addressLine2(), customer.city(),
                customer.region(), customer.postalCode(), customer.countryCode());
        return ClientSupport.call(breaker, "Shipping", () -> restClient.post()
                .uri("/internal/shipments")
                .body(new CreateRequest(orderId, customer.id(), address))
                .retrieve()
                .body(ShipmentResult.class));
    }

    @Override
    public void cancel(UUID orderId) {
        try {
            restClient.delete()
                    .uri("/internal/shipments/{orderId}", orderId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw ClientSupport.failure("Shipping", e);
        }
    }
}
