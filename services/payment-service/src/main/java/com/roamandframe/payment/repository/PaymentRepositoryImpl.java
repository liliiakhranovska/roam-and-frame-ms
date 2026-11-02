package com.roamandframe.payment.repository;

import com.roamandframe.payment.model.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    PaymentRepositoryImpl(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment create(UUID orderId, UUID customerId, BigDecimal amount, String reference) {
        PaymentJpaEntity e = jpaRepository.saveAndFlush(new PaymentJpaEntity(orderId, customerId, amount, reference));
        return toPayment(e);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrderId(orderId).map(this::toPayment);
    }

    private Payment toPayment(PaymentJpaEntity paymentJpaEntity) {
        return new Payment(paymentJpaEntity.getId(), paymentJpaEntity.getOrderId(), paymentJpaEntity.getCustomerId(),
                paymentJpaEntity.getAmount(), paymentJpaEntity.getStatus(), paymentJpaEntity.getReference());
    }

    @Override
    public void markVoided(UUID orderId) {
        jpaRepository.findByOrderId(orderId).ifPresent(PaymentJpaEntity::markVoided);
    }
}
