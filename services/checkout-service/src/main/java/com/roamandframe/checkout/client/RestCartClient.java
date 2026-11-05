package com.roamandframe.checkout.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.UUID;

@Component
class RestCartClient implements CartClient {

    private final RestClient restClient;

    RestCartClient(@Value("${cart.url}") String baseUrl,
                   @Value("${cart.timeout}") Duration timeout) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
    }

    @Override
    public CartContents validate(UUID customerId) {
        try {
            return restClient.post()
                    .uri("/internal/carts/{customerId}/validation", customerId)
                    .retrieve()
                    .body(CartContents.class);
        } catch (RestClientException e) {
            throw ClientSupport.failure("Cart", e);
        }
    }

    @Override
    public void clear(UUID customerId) {
        try {
            restClient.delete()
                    .uri("/internal/carts/{customerId}", customerId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw ClientSupport.failure("Cart", e);
        }
    }
}
