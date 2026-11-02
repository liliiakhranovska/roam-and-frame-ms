package com.roamandframe.inventory.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, UUID> {

    List<ReservationJpaEntity> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);

    boolean existsByOrderIdAndStatus(UUID orderId, String status);
}
