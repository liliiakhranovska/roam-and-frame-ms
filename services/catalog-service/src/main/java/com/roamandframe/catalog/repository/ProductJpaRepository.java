package com.roamandframe.catalog.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ProductJpaRepository
        extends JpaRepository<ProductJpaEntity, UUID>, JpaSpecificationExecutor<ProductJpaEntity> {

    @Override
    @EntityGraph(attributePaths = "category")
    List<ProductJpaEntity> findAll(Specification<ProductJpaEntity> spec, Sort sort);

    @EntityGraph(attributePaths = "category")
    Optional<ProductJpaEntity> findBySku(String sku);

    @EntityGraph(attributePaths = "category")
    List<ProductJpaEntity> findBySkuIn(Collection<String> skus);
}

