package com.roamandframe.checkout.client;

import com.roamandframe.checkout.exception.StepUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
class CircuitBreakerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerConfiguration.class);

    @Bean
    CircuitBreakerRegistry circuitBreakerRegistry(
            @Value("${circuitbreaker.window-size}") int windowSize,
            @Value("${circuitbreaker.minimum-calls}") int minimumCalls,
            @Value("${circuitbreaker.failure-rate}") float failureRate,
            @Value("${circuitbreaker.open-duration}") Duration openDuration,
            @Value("${circuitbreaker.trial-calls}") int trialCalls) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(windowSize)
                .minimumNumberOfCalls(minimumCalls)
                .failureRateThreshold(failureRate)
                .waitDurationInOpenState(openDuration)
                .permittedNumberOfCallsInHalfOpenState(trialCalls)
                .recordExceptions(StepUnavailableException.class)   // refusals (402, 409) are not failures
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        registry.getEventPublisher().onEntryAdded(added -> added.getAddedEntry().getEventPublisher()
                .onStateTransition(event -> log.warn("Circuit breaker '{}': {} -> {}",
                        event.getCircuitBreakerName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState())));
        return registry;
    }
}
