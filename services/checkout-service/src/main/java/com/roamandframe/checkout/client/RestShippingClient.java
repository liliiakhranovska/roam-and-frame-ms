package com.roamandframe.checkout.client;

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

    RestShippingClient(@Value("${shipping.url}") String baseUrl,
                       @Value("${shipping.timeout}") Duration timeout) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
    }

    @Override
    public ShipmentResult create(UUID orderId, CustomerProfile customer) {
        Address address = new Address(customer.addressLine1(), customer.addressLine2(), customer.city(),
                customer.region(), customer.postalCode(), customer.countryCode());
        try {
            return restClient.post()
                    .uri("/internal/shipments")
                    .body(new CreateRequest(orderId, customer.id(), address))
                    .retrieve()
                    .body(ShipmentResult.class);
        } catch (RestClientException e) {
            throw ClientSupport.failure("Shipping", e);
        }
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
