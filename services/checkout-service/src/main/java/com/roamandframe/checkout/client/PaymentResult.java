package com.roamandframe.checkout.client;

import java.util.UUID;

public record PaymentResult(UUID orderId, String status, String reference) {
}
