package com.roamandframe.inventory.service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface InventoryService {

    Map<String, Integer> getQuantities(Collection<String> skus);

    void reserve(UUID orderId, Map<String, Integer> quantities);

    void release(UUID orderId);
}
