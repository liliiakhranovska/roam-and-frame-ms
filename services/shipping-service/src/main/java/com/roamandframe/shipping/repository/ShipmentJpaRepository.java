package com.roamandframe.shipping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ShipmentJpaRepository extends JpaRepository<ShipmentJpaEntity, UUID> {
    List<ShipmentJpaEntity> findByOrderIdIn(Collection<UUID> orderIds);
    Optional<ShipmentJpaEntity> findByOrderId(UUID orderId);
}
