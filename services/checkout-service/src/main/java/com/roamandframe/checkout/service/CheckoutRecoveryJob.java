package com.roamandframe.checkout.service;

import com.roamandframe.checkout.model.Checkout;
import com.roamandframe.checkout.repository.CheckoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
class CheckoutRecoveryJob {

    private static final Logger log = LoggerFactory.getLogger(CheckoutRecoveryJob.class);

    private final CheckoutRepository repository;
    private final CheckoutService checkoutService;
    private final Duration staleAfter;

    CheckoutRecoveryJob(CheckoutRepository repository, CheckoutService checkoutService,
                        @Value("${checkout.stale-after}") Duration staleAfter) {
        this.repository = repository;
        this.checkoutService = checkoutService;
        this.staleAfter = staleAfter;
    }

    @Scheduled(fixedDelayString = "${checkout.recovery-interval}")
    void recoverStaleCheckouts() {
        for (Checkout stale : repository.findStale(Instant.now().minus(staleAfter))) {
            if (!repository.claim(stale)) {
                continue;
            }
            log.info("Recovering checkout {} ({}, last step {})", stale.id(), stale.status(), stale.lastStep());
            try {
                checkoutService.recover(stale);
            } catch (RuntimeException e) {
                log.error("Recovery of checkout {} failed, will retry", stale.id(), e);
            }
        }
    }
}
