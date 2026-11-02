package com.roamandframe.inventory.repository;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface InventoryRepository {

    Map<String, Integer> findQuantities(Collection<String> skus);

    boolean reserve(String sku, int quantity);

    void saveReservation(UUID orderId, String sku, int quantity);

    boolean hasReservation(UUID orderId);

    boolean hasActiveReservation(UUID orderId);

    void releaseReservations(UUID orderId);
}
