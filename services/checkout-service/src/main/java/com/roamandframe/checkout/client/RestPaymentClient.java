package com.roamandframe.checkout.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Component
class RestPaymentClient implements PaymentClient {

    private record AuthorizeRequest(UUID orderId, UUID customerId, BigDecimal amount) {
    }

    private final RestClient restClient;

    RestPaymentClient(@Value("${payment.url}") String baseUrl,
                      @Value("${payment.timeout}") Duration timeout) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
    }

    @Override
    public PaymentResult authorize(UUID orderId, UUID customerId, BigDecimal amount) {
        try {
            return restClient.post()
                    .uri("/internal/payments/authorizations")
                    .body(new AuthorizeRequest(orderId, customerId, amount))
                    .retrieve()
                    .body(PaymentResult.class);
        } catch (RestClientException e) {
            throw ClientSupport.failure("Payment", e);
        }
    }

    @Override
    public void voidAuthorization(UUID orderId) {
        try {
            restClient.delete()
                    .uri("/internal/payments/authorizations/{orderId}", orderId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw ClientSupport.failure("Payment", e);
        }
    }
}
