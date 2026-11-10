package com.roamandframe.checkout.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
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
    private final CircuitBreaker breaker;

    RestPaymentClient(@Value("${payment.url}") String baseUrl,
                      @Value("${payment.timeout}") Duration timeout,
                      CircuitBreakerRegistry registry) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
        this.breaker =  registry.circuitBreaker("payment");
    }

    @Override
    public PaymentResult authorize(UUID orderId, UUID customerId, BigDecimal amount) {
        return ClientSupport.call(breaker, "Payment", () -> restClient.post()
                .uri("/internal/payments/authorizations")
                .body(new AuthorizeRequest(orderId, customerId, amount))
                .retrieve()
                .body(PaymentResult.class));
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
