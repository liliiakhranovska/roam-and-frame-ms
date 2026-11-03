package com.roamandframe.catalog.repository;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "products")
class ProductJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private CategoryJpaEntity category;

    private String brand;

    @Column(nullable = false)
    private BigDecimal price;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ProductJpaEntity() {
    }

    UUID getId() { return id; }
    String getSku() { return sku; }
    String getName() { return name; }
    String getDescription() { return description; }
    CategoryJpaEntity getCategory() { return category; }
    String getBrand() { return brand; }
    BigDecimal getPrice() { return price; }
    Map<String, Object> getAttributes() { return attributes; }
}
