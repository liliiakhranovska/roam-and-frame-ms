package com.roamandframe.checkout.repository;

import com.roamandframe.checkout.exception.CheckoutInProgressException;
import com.roamandframe.checkout.model.Checkout;
import com.roamandframe.checkout.model.CheckoutStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class CheckoutRepositoryImpl implements CheckoutRepository {

    private static final List<CheckoutStatus> ACTIVE =
            List.of(CheckoutStatus.RUNNING, CheckoutStatus.COMPENSATING, CheckoutStatus.FINISHING);
    private static final int MAX_REASON_LENGTH = 500;

    private final CheckoutJpaRepository jpaRepository;

    CheckoutRepositoryImpl(CheckoutJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Checkout start(UUID id, UUID customerId) {
        try {
            return jpaRepository.saveAndFlush(new CheckoutJpaEntity(id, customerId)).toModel();
        } catch (DataIntegrityViolationException e) {
            throw new CheckoutInProgressException(customerId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Checkout> findById(UUID id) {
        return jpaRepository.findById(id).map(CheckoutJpaEntity::toModel);
    }

    @Transactional
    @Override
    public void recordStep(UUID id, int step) {
        jpaRepository.updateStep(id, step, Instant.now());
    }

    @Transactional
    @Override
    public void setStatus(UUID id, CheckoutStatus status, String failureReason) {
        String reason = failureReason != null && failureReason.length() > MAX_REASON_LENGTH
                ? failureReason.substring(0, MAX_REASON_LENGTH)
                : failureReason;
        jpaRepository.updateStatus(id, status, reason, Instant.now());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Checkout> findStale(Instant before) {
        return jpaRepository.findByStatusInAndUpdatedAtBefore(ACTIVE, before).stream()
                .map(CheckoutJpaEntity::toModel)
                .toList();
    }

    @Transactional
    @Override
    public boolean claim(Checkout stale) {
        return jpaRepository.touchIfUnchanged(stale.id(), stale.updatedAt(), Instant.now()) == 1;
    }
}
