package com.roamandframe.catalog.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;

@Component
class RestInventoryClient implements InventoryClient {

    private static final ParameterizedTypeReference<Map<String, Integer>> QUANTITIES =
            new ParameterizedTypeReference<>() { };

    private final RestClient restClient;

    RestInventoryClient(@Value("${inventory.url}") String baseUrl,
                        @Value("${inventory.timeout}") Duration timeout) {
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
    public Map<String, Integer> getQuantities(Collection<String> skus) {
        if (skus.isEmpty()) {
            return Map.of();
        }
        try {
            return restClient.get()
                    .uri(uri -> uri.path("/internal/inventory/quantities").queryParam("skus", skus).build())
                    .retrieve()
                    .body(QUANTITIES);
        } catch (RestClientException e) {
            throw new InventoryUnavailableException(e);
        }
    }
}
