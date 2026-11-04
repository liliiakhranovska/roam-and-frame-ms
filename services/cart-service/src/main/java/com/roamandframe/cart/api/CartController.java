package com.roamandframe.cart.api;

import com.roamandframe.cart.security.CurrentCustomerId;
import com.roamandframe.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
class CartController {

    private final CartService cartService;

    CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    CartResponse getCart(@CurrentCustomerId UUID customerId) {
        return CartResponse.from(cartService.getCart(customerId));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void clearCart(@CurrentCustomerId UUID customerId) {
        cartService.clearCart(customerId);
    }

    @PutMapping("/items/{sku}")
    CartResponse setItemQuantity(@CurrentCustomerId UUID customerId, @PathVariable String sku,
                                 @Valid @RequestBody SetCartItemQuantityRequest request) {
        return CartResponse.from(cartService.setItemQuantity(customerId, sku, request.quantity()));
    }
}
