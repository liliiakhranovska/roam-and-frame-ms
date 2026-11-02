package com.roamandframe.inventory.repository;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
class InventoryRepositoryImpl implements InventoryRepository {

    private final StockItemJpaRepository stockRepository;
    private final ReservationJpaRepository reservationRepository;

    InventoryRepositoryImpl(StockItemJpaRepository stockRepository,
                            ReservationJpaRepository reservationRepository) {
        this.stockRepository = stockRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Map<String, Integer> findQuantities(Collection<String> skus) {
        return stockRepository.findAllById(skus).stream()
                .collect(Collectors.toMap(StockItemJpaEntity::getSku, StockItemJpaEntity::getQuantity));
    }

    @Override
    public boolean reserve(String sku, int quantity) {
        return stockRepository.reserve(sku, quantity) > 0;
    }

    @Override
    public void saveReservation(UUID orderId, String sku, int quantity) {
        reservationRepository.save(new ReservationJpaEntity(orderId, sku, quantity));
    }

    @Override
    public boolean hasReservation(UUID orderId) {
        return reservationRepository.existsByOrderId(orderId);
    }

    @Override
    public boolean hasActiveReservation(UUID orderId) {
        return reservationRepository.existsByOrderIdAndStatus(orderId, "RESERVED");
    }

    @Override
    public void releaseReservations(UUID orderId) {
        for (ReservationJpaEntity reservation : reservationRepository.findByOrderId(orderId)) {
            if (reservation.isReserved()) {
                stockRepository.release(reservation.getSku(), reservation.getQuantity());
                reservation.markReleased();
            }
        }
    }
}
