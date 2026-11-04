package com.roamandframe.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, UUID> {
    Optional<CustomerJpaEntity> findByEmail(String email);
}
