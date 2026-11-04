package com.roamandframe.cart.service;

import com.roamandframe.cart.model.Cart;

import java.util.UUID;

public interface CartService {

    Cart getCart(UUID customerId);

    Cart setItemQuantity(UUID customerId, String sku, int quantity);

    Cart validateCart(UUID customerId);

    void clearCart(UUID customerId);


}
