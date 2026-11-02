package com.roamandframe.payment.service;

import com.roamandframe.payment.exception.PaymentVoidedException;
import com.roamandframe.payment.gateway.PaymentGateway;
import com.roamandframe.payment.model.Payment;
import com.roamandframe.payment.model.PaymentStatus;
import com.roamandframe.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }

    public Payment authorize(UUID orderId, UUID customerId, BigDecimal amount) {
        Optional<Payment> existing = paymentRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            Payment payment = existing.get();
            if (payment.status() == PaymentStatus.VOIDED) {
                throw new PaymentVoidedException(orderId);
            }
            return payment; // a retry of a call that already succeeded
        }
        String reference = paymentGateway.authorize(orderId, amount);
        try {
            return paymentRepository.create(orderId, customerId, amount, reference);
        } catch (RuntimeException e) {
            paymentGateway.voidAuthorization(reference);
            throw e;
        }
    }

    @Transactional
    @Override
    public void voidAuthorization(UUID orderId) {
        paymentRepository.findByOrderId(orderId)
                .filter(payment -> payment.status() == PaymentStatus.AUTHORIZED)
                .ifPresent(payment -> {
                    paymentGateway.voidAuthorization(payment.reference());
                    paymentRepository.markVoided(orderId);
                });
    }
}

