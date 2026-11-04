package com.roamandframe.cart.api;

import com.roamandframe.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/carts")
class InternalCartController {

    private final CartService cartService;

    InternalCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{customerId}/validation")
    CartResponse validate(@PathVariable UUID customerId) {
        return CartResponse.from(cartService.validateCart(customerId));
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void clear(@PathVariable UUID customerId) {
        cartService.clearCart(customerId);
    }
}