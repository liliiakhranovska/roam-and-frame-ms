package com.roamandframe.mcp.client;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ProblemDetail;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/** Read-only access to the platform services, on behalf of the customer whose token is passed in. */
@Component
public class PlatformClient {

    private static final int ATTEMPTS = 2;   // one retry; safe because every call is a GET

    private final Map<Backend, RestClient> clients = new EnumMap<>(Backend.class);
    private final Map<Backend, CircuitBreaker> breakers = new EnumMap<>(Backend.class);

    PlatformClient(@LoadBalanced RestClient.Builder builder,
                   CircuitBreakerRegistry registry,
                   @Value("${platform.catalog-url}") String catalogUrl,
                   @Value("${platform.orders-url}") String ordersUrl,
                   @Value("${platform.cart-url}") String cartUrl,
                   @Value("${platform.customer-url}") String customerUrl,
                   @Value("${platform.timeout}") Duration timeout) {
        Map<Backend, String> urls = Map.of(
                Backend.CATALOG, catalogUrl, Backend.ORDERS, ordersUrl,
                Backend.CART, cartUrl, Backend.CUSTOMER, customerUrl);
        urls.forEach((backend, url) -> {
            clients.put(backend, create(builder.clone(), url, timeout));
            breakers.put(backend, registry.circuitBreaker(backend.label()));
        });
    }

    public <T> T get(Backend backend, String authorization, Function<UriBuilder, URI> uri,
                     ParameterizedTypeReference<T> type) {
        if (authorization == null) {
            throw new ToolException("Not authenticated: the request carried no credentials.");
        }
        RestClient client = clients.get(backend);
        CircuitBreaker breaker = breakers.get(backend);
        for (int attempt = 1; ; attempt++) {
            try {
                return breaker.executeSupplier(() -> call(backend, client, authorization, uri, type));
            } catch (CallNotPermittedException e) {
                throw new PlatformUnavailableException(backend, e);   // breaker open: do not retry
            } catch (PlatformUnavailableException e) {
                if (attempt >= ATTEMPTS) {
                    throw e;
                }
            }
        }
    }

    private <T> T call(Backend backend, RestClient client, String authorization,
                       Function<UriBuilder, URI> uri, ParameterizedTypeReference<T> type) {
        try {
            return client.get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .retrieve()
                    .body(type);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                throw new PlatformUnavailableException(backend, e);
            }
            throw refusal(e);
        } catch (RestClientException | IllegalStateException e) {   // timeout, connection refused, no instance in Eureka
            throw new PlatformUnavailableException(backend, e);
        }
    }

    private static ToolException refusal(RestClientResponseException e) {
        int status = e.getStatusCode().value();
        if (status == 401 || status == 403) {
            return new ToolException("This session is not authorized for the request.");
        }
        if (status == 404) {
            return new ToolException("Not found.");
        }
        return new ToolException("The request was rejected: " + detail(e));
    }

    private static String detail(RestClientResponseException e) {
        try {
            ProblemDetail problem = e.getResponseBodyAs(ProblemDetail.class);
            if (problem != null && problem.getDetail() != null) {
                return problem.getDetail();
            }
        } catch (RuntimeException ignored) {
            // not a ProblemDetail body
        }
        return "invalid request";
    }

    private static RestClient create(RestClient.Builder builder, String baseUrl, Duration timeout) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(timeout)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);
        return builder.baseUrl(baseUrl).requestFactory(requestFactory).build();
    }
}