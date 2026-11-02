package com.roamandframe.inventory.service;

import com.roamandframe.inventory.exception.ReservationReleasedException;
import com.roamandframe.inventory.exception.StockReservationException;
import com.roamandframe.inventory.repository.InventoryRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, Integer> getQuantities(Collection<String> skus) {
        Map<String, Integer> quantities = new HashMap<>(inventoryRepository.findQuantities(skus));
        skus.forEach(sku -> quantities.putIfAbsent(sku, 0));
        return quantities;
    }

    @Transactional
    @Override
    public void reserve(UUID orderId, Map<String, Integer> quantities) {
        if (inventoryRepository.hasReservation(orderId)) {
            if (inventoryRepository.hasActiveReservation(orderId)) {
                return; // a retry of a call that already succeeded
            }
            throw new ReservationReleasedException(orderId);
        }
        // fixed SKU order, so concurrent reservations lock rows in the same order
        new TreeMap<>(quantities).forEach((sku, quantity) -> {
            if (!inventoryRepository.reserve(sku, quantity)) {
                throw new StockReservationException(sku, quantity);
            }
            inventoryRepository.saveReservation(orderId, sku, quantity);
        });
    }

    @Transactional
    @Override
    public void release(UUID orderId) {
        inventoryRepository.releaseReservations(orderId);
    }
}
