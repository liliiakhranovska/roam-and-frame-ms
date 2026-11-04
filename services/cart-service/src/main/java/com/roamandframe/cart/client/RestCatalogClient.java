package com.roamandframe.cart.client;

import com.roamandframe.cart.exception.CatalogUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
class RestCatalogClient implements CatalogClient {

    private final RestClient restClient;

    RestCatalogClient(@Value("${catalog.url}") String baseUrl,
                        @Value("${catalog.timeout}") Duration timeout) {
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
    public Map<String, CatalogProduct> getProducts(Collection<String> skus) {
        if (skus.isEmpty()) {
            return Map.of();
        }
        try {
            List<CatalogProduct> products = restClient.get()
                    .uri(uri -> uri.path("/internal/catalog/products").queryParam("skus", skus).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CatalogProduct>>() { });
            return products.stream().collect(Collectors.toMap(CatalogProduct::sku, Function.identity()));
        } catch (RestClientException e) {
            throw new CatalogUnavailableException(e);
        }
    }
}
