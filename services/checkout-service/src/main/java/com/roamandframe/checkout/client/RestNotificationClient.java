package com.roamandframe.checkout.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
class RestNotificationClient implements NotificationClient {

    private record Customer(String firstName, String email, String addressLine1, String addressLine2,
                            String postalCode, String city, String countryCode) {
    }

    private record Item(String name, int quantity, BigDecimal lineTotal) {
    }

    private record Order(UUID id, Instant createdAt, BigDecimal total, List<Item> items) {
    }

    private record Payment(String status, String reference) {
    }

    private record Confirmation(Customer customer, Order order, Payment payment, String trackingNumber) {
    }

    private final RestClient restClient;
    private final CircuitBreaker breaker;

    RestNotificationClient(@Value("${notification.url}") String baseUrl,
                           @Value("${notification.timeout}") Duration timeout,
                           CircuitBreakerRegistry registry) {
        this.restClient = ClientSupport.restClient(baseUrl, timeout);
        this.breaker = registry.circuitBreaker("notification");
    }

    @Override
    public void sendOrderConfirmation(CustomerProfile customer, PlacedOrder order,
                                      PaymentResult payment, ShipmentResult shipment) {
        Confirmation body = new Confirmation(
                new Customer(customer.firstName(), customer.email(), customer.addressLine1(),
                        customer.addressLine2(), customer.postalCode(), customer.city(), customer.countryCode()),
                new Order(order.id(), order.createdAt(), order.total(),
                        order.items().stream().map(i -> new Item(i.name(), i.quantity(), i.lineTotal())).toList()),
                new Payment(payment.status(), payment.reference()),
                shipment.trackingNumber());
        ClientSupport.run(breaker, "Notification", () -> restClient.post()
                .uri("/internal/notifications/order-confirmation")
                .body(body)
                .retrieve()
                .toBodilessEntity());
        try {
            restClient.post()
                    .uri("/internal/notifications/order-confirmation")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw ClientSupport.failure("Notification", e);
        }
    }
}
