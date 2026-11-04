package com.roamandframe.cart.repository;

import com.roamandframe.cart.model.CartItem;

import java.util.List;
import java.util.UUID;

public interface CartRepository {
    List<CartItem> findItems(UUID customerId);
    void saveItem(UUID customerId, String sku, int quantity);
    void deleteItem(UUID customerId, String sku);
    void deleteAll(UUID customerId);
}
