package com.roamandframe.payment.api;

import com.roamandframe.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/payments")
class PaymentController {

    private final PaymentService paymentService;

    PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/authorizations")
    PaymentResponse authorize(@Valid @RequestBody AuthorizePaymentRequest request) {
        return PaymentResponse.from(
                paymentService.authorize(request.orderId(), request.customerId(), request.amount()));
    }

    @DeleteMapping("/authorizations/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void voidAuthorization(@PathVariable UUID orderId) {
        paymentService.voidAuthorization(orderId);
    }
}
