package com.roamandframe.order.repository;

import com.roamandframe.order.model.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID>, JpaSpecificationExecutor<OrderJpaEntity> {

    @EntityGraph(attributePaths = "items")
    List<OrderJpaEntity> findByCustomerIdAndStatusNotOrderByCreatedAtDesc(UUID customerId, OrderStatus status);

    @EntityGraph(attributePaths = "items")
    List<OrderJpaEntity> findByIdIn(Collection<UUID> ids);
}
