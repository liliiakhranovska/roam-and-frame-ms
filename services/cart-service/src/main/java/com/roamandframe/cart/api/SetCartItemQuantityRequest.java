package com.roamandframe.cart.api;

import jakarta.validation.constraints.Min;

public record SetCartItemQuantityRequest(@Min(0) int quantity) {
}
