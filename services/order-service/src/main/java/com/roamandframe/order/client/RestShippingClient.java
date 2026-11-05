package com.roamandframe.order.client;

import com.roamandframe.order.exception.ShipmentRejectedException;
import com.roamandframe.order.exception.ShippingUnavailableException;
import com.roamandframe.order.model.ShipmentInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
class RestShippingClient implements ShippingClient {

    private final RestClient restClient;

    RestShippingClient(@Value("${shipping.url}") String baseUrl,
                       @Value("${shipping.timeout}") Duration timeout) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(timeout)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public Map<UUID, ShipmentInfo> findByOrderIds(Collection<UUID> orderIds) {
        if (orderIds.isEmpty()) {
            return Map.of();
        }
        try {
            List<ShipmentInfo> shipments = restClient.get()
                    .uri(uri -> uri.path("/internal/shipments").queryParam("orderIds", orderIds).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ShipmentInfo>>() { });
            return shipments.stream().collect(Collectors.toMap(ShipmentInfo::orderId, Function.identity()));
        } catch (RestClientException e) {
            throw new ShippingUnavailableException(e);
        }
    }

    @Override
    public ShipmentInfo ship(UUID orderId) {
        try {
            return restClient.post()
                    .uri("/internal/shipments/{orderId}/ship", orderId)
                    .retrieve()
                    .body(ShipmentInfo.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new ShipmentRejectedException(orderId);
            }
            throw new ShippingUnavailableException(e);
        } catch (RestClientException e) {
            throw new ShippingUnavailableException(e);
        }
    }
}

