package com.roamandframe.shipping.repository;

import com.roamandframe.shipping.model.Shipment;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class ShipmentRepositoryImpl implements ShipmentRepository {

    private final ShipmentJpaRepository jpaRepository;

    ShipmentRepositoryImpl(ShipmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Shipment create(UUID orderId, UUID customerId, String trackingNumber) {
        return toShipment(jpaRepository.saveAndFlush(new ShipmentJpaEntity(orderId, customerId, trackingNumber)));
    }

    @Override
    public List<Shipment> findByOrderIds(Collection<UUID> orderIds) {
        return jpaRepository.findByOrderIdIn(orderIds).stream().map(this::toShipment).toList();
    }

    private Shipment toShipment(ShipmentJpaEntity e) {
        return new Shipment(e.getId(), e.getOrderId(), e.getCustomerId(), e.getStatus(), e.getTrackingNumber());
    }

    @Override
    public void markShipped(UUID orderId) {
        jpaRepository.findByOrderId(orderId).ifPresent(ShipmentJpaEntity::ship);
    }

    @Override
    public Optional<Shipment> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrderId(orderId).map(this::toShipment);
    }

    @Override
    public void markCancelled(UUID orderId) {
        jpaRepository.findByOrderId(orderId).ifPresent(ShipmentJpaEntity::cancel);
    }
}

