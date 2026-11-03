package com.roamandframe.catalog.model;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummary(UUID id, String sku, String name, String brand,
                             BigDecimal price, String categoryCode) {
}
