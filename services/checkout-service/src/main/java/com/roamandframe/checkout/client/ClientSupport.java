package com.roamandframe.checkout.client;

import com.roamandframe.checkout.exception.StepRejectedException;
import com.roamandframe.checkout.exception.StepUnavailableException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;

final class ClientSupport {

    private ClientSupport() {
    }

    static RestClient restClient(String baseUrl, Duration timeout) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(timeout)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    /** 402 and 409 are business refusals; anything else (timeout, 5xx, other 4xx) means the service is unusable. */
    static RuntimeException failure(String service, RestClientException e) {
        if (e instanceof RestClientResponseException response) {
            int status = response.getStatusCode().value();
            if (status == 402 || status == 409) {
                return new StepRejectedException(status, detail(response));
            }
        }
        return new StepUnavailableException(service, e);
    }

    private static String detail(RestClientResponseException e) {
        try {
            ProblemDetail problem = e.getResponseBodyAs(ProblemDetail.class);
            if (problem != null && problem.getDetail() != null) {
                return problem.getDetail();
            }
        } catch (RuntimeException ignored) {
            // body is not a ProblemDetail; fall through to the generic message
        }
        return "The request was refused";
    }
}
