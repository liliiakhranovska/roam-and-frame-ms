package com.roamandframe.gateway.client;

import com.roamandframe.gateway.exception.CustomerUnavailableException;
import com.roamandframe.gateway.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

@Component
class RestCustomerClient implements CustomerClient {

    private final RestClient restClient;

    RestCustomerClient(@Value("${customer.url}") String baseUrl,
                       @Value("${customer.timeout}") Duration timeout) {
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
    public AuthenticatedCustomer authenticate(String email, String password) {
        try {
            return restClient.post()
                    .uri("/internal/customers/authentication")
                    .body(Map.of("email", email, "password", password))
                    .retrieve()
                    .body(AuthenticatedCustomer.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 401) {
                throw new InvalidCredentialsException();
            }
            throw new CustomerUnavailableException(e);
        } catch (RestClientException e) {
            throw new CustomerUnavailableException(e);
        }
    }
}
