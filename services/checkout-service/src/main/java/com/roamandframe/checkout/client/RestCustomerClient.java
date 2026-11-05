package com.roamandframe.checkout.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.UUID;

@Component
class RestCustomerClient implements CustomerClient {

    private final RestClient restClient;

    RestCustomerClient(@Value("${customer.url}") String baseUrl,
                       @Value("${customer.timeout}") Duration timeout) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
    }

    @Override
    public CustomerProfile getProfile(UUID customerId) {
        try {
            return restClient.get()
                    .uri("/internal/customers/{id}", customerId)
                    .retrieve()
                    .body(CustomerProfile.class);
        } catch (RestClientException e) {
            throw ClientSupport.failure("Customer", e);
        }
    }
}
