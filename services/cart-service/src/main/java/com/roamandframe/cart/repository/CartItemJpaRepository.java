package com.roamandframe.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface CartItemJpaRepository extends JpaRepository<CartItemJpaEntity, UUID> {
    List<CartItemJpaEntity> findByCustomerIdOrderByCreatedAt(UUID customerId);

    Optional<CartItemJpaEntity> findByCustomerIdAndSku(UUID customerId, String sku);

    void deleteByCustomerIdAndSku(UUID customerId, String sku);

    void deleteByCustomerId(UUID customerId);
}
