package com.roamandframe.catalog.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "categories")
class CategoryJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    protected CategoryJpaEntity() {
    }

    UUID getId() { return id; }
    String getCode() { return code; }
    String getName() { return name; }
}

